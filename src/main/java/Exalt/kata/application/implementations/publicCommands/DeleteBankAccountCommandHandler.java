package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.DeleteBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.DeleteBankAccountCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteBankAccountCommandHandler implements IPublicCommandHandler<DeleteBankAccountCommandRequest, DeleteBankAccountCommandResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterBankAccountExternalId = "bankAccountId";

    private final IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository;

    public DeleteBankAccountCommandHandler(IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> repository)
    {
        bankAccountAggregateRootRepository = repository;
    }

    /**
     * We don't want to delete the bank account directly from the database,
     * but rather use the status to express the fact that a bank account has been deleted.
     * In a complete application, we'd have a batch that deletes all bank accounts in Deleted status
     * for X amount of time (X being a retention time determined by the product and legal teams).
     */
    public Result<DeleteBankAccountCommandResponse, PublicError> handleCommand(DeleteBankAccountCommandRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.bankAccountExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterBankAccountExternalId), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var clientExternalId = clientExternalIdResult.value();

        var bankAccountExternalIdResult = BankAccountAggregateExternalId.create(request.bankAccountExternalId);
        if (bankAccountExternalIdResult.isFailed()) return Result.fail(new PublicError(bankAccountExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var bankAccountExternalId = bankAccountExternalIdResult.value();

        var bankAccountAggregateResult = bankAccountAggregateRootRepository.getByExternalId(bankAccountExternalId);
        if (bankAccountAggregateResult.isFailed()) return Result.fail(new PublicError(bankAccountAggregateResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var bankAccountAggregate = bankAccountAggregateResult.value();

        if (!bankAccountAggregate.getClientExternalId().equals(clientExternalId))
            return Result.fail(new PublicError(String.format("Client %s is not the owner of the %s bank account", clientExternalId.value(), bankAccountExternalId.value()), ErrorCode.client_is_not_bank_account_owner));

        var deleteBankAccountResult = bankAccountAggregate.delete();
        if (deleteBankAccountResult.isFailed()) return Result.fail(new PublicError(deleteBankAccountResult.getFirstErrorMessage(), ErrorCode.bank_account_error));
        var saveBankAccountDeletion = bankAccountAggregateRootRepository.saveAggregate(bankAccountAggregate);
        if (saveBankAccountDeletion.isFailed()) return Result.fail(new PublicError(saveBankAccountDeletion.getFirstErrorMessage(), ErrorCode.internal));

        return Result.ok(new DeleteBankAccountCommandResponse(OperationResult.Success.code(), OperationResult.Success.message()));
    }

}
