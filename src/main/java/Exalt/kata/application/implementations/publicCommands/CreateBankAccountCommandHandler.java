package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.CreateBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateBankAccountCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateBankAccountCommandHandler implements IPublicCommandHandler<CreateBankAccountCommandRequest, CreateBankAccountCommandResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterCurrency = "currency";
    private final IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    private final IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository;

    public CreateBankAccountCommandHandler(
        IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository,
        IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository)
    {
        this.clientAggregateRootRepository = clientAggregateRootRepository;
        this.bankAccountAggregateRootRepository = bankAccountAggregateRootRepository;
    }

    public Result<CreateBankAccountCommandResponse, PublicError> handleCommand(CreateBankAccountCommandRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.currency == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterCurrency), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));

        Currency currency;
        try
        {
            currency = Currency.valueOf(request.currency);
        } catch (IllegalArgumentException e)
        {
            return Result.fail(new PublicError(String.format("%s is not a valid Currency", request.currency), ErrorCode.invalid_currency));
        }

        Tag tag = null;
        if (request.tag != null)
        {
            var tagResult = Tag.create(request.tag);
            if (tagResult.isFailed()) return Result.fail(new PublicError(tagResult.getFirstErrorMessage(), ErrorCode.param_error));

            tag = tagResult.value();
        }

        var clientAggregateResult = clientAggregateRootRepository.getByExternalId(clientExternalIdResult.value());
        if (clientAggregateResult.isFailed()) return Result.fail(new PublicError(clientAggregateResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var clientAggregate = clientAggregateResult.value();
        if (clientAggregate.getStatus().equals(ClientStatus.DELETED)) return Result.fail(new PublicError(String.format("Client %s is Deleted", clientAggregate.getExternalId().value()), ErrorCode.client_error));

        var bankAccountAggregateResult = BankAccountAggregate.create(
            BankAccountAggregateId.create(),
            BankAccountAggregateExternalId.create(),
            clientExternalIdResult.value(),
            currency,
            tag);
        if (bankAccountAggregateResult.isFailed()) return Result.fail(new PublicError(bankAccountAggregateResult.getFirstErrorMessage(), ErrorCode.bank_account_error));
        var bankAccountAggregate = bankAccountAggregateResult.value();

        if (request.overdrawnAuthorized > 0)
        {
            var overdrawnAmountResult = Amount.createForOverdrawn(request.overdrawnAuthorized);
            if (overdrawnAmountResult.isFailed()) return Result.fail(new PublicError(overdrawnAmountResult.getFirstErrorMessage(), ErrorCode.bank_account_error));
            bankAccountAggregate.authorizeOverdrawn(overdrawnAmountResult.value());
        }
        var saveAfterCreationResult = bankAccountAggregateRootRepository.saveAggregate(bankAccountAggregate);
        if (saveAfterCreationResult.isFailed()) return Result.fail(new PublicError(saveAfterCreationResult.getFirstErrorMessage(), ErrorCode.internal));

        return Result.ok(buildCommandResponse(bankAccountAggregate));
    }

    private CreateBankAccountCommandResponse buildCommandResponse(BankAccountAggregate bankAccountAggregate)
    {
        return new CreateBankAccountCommandResponse(
            bankAccountAggregate.getExternalId().value(),
            bankAccountAggregate.getClientExternalId().value(),
            bankAccountAggregate.getCreationDate(),
            bankAccountAggregate.getStatus().name(),
            bankAccountAggregate.getAmount().value(),
            bankAccountAggregate.getCurrency().name(),
            bankAccountAggregate.getTag().value());
    }
}
