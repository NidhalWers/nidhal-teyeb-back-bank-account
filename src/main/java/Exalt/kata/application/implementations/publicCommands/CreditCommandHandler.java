package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.CreditCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreditCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.Tag;
import exalt.kata.domain.accounting.AccountingTransactionAggregate;
import exalt.kata.domain.accounting.AccountingTransactionAggregateExternalId;
import exalt.kata.domain.accounting.AccountingTransactionAggregateId;
import exalt.kata.domain.accounting.TransactionType;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.Action;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import exalt.kata.infrastructure.primitives.persistence.IUnitOfWork;
import org.springframework.stereotype.Component;

import static exalt.kata.domain.core.primitives.results.base.Result.*;

@Component
public class CreditCommandHandler implements IPublicCommandHandler<CreditCommandRequest, CreditCommandResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterBankAccountExternalId = "bankAccountId";
    private static final String ParameterCurrency = "currency";
    private final IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    private final IAggregateRootRepository<AccountingTransactionAggregate, AccountingTransactionAggregateId, AccountingTransactionAggregateExternalId> accountingTransactionAggregateRootRepository;
    private final IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository;
    private final IUnitOfWork unitOfWork;

    public CreditCommandHandler(
        IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository,
        IAggregateRootRepository<AccountingTransactionAggregate, AccountingTransactionAggregateId, AccountingTransactionAggregateExternalId> accountingTransactionAggregateRootRepository,
        IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository,
        IUnitOfWork work)
    {
        this.clientAggregateRootRepository = clientAggregateRootRepository;
        this.accountingTransactionAggregateRootRepository = accountingTransactionAggregateRootRepository;
        this.bankAccountAggregateRootRepository = bankAccountAggregateRootRepository;
        unitOfWork = work;
    }

    public Result<CreditCommandResponse, PublicError> handleCommand(CreditCommandRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.bankAccountId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterBankAccountExternalId), ErrorCode.param_error));
        if (request.currency == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterCurrency), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var clientExternalId = clientExternalIdResult.value();

        var bankAccountExternalIdResult = BankAccountAggregateExternalId.create(request.bankAccountId);
        if (bankAccountExternalIdResult.isFailed()) return Result.fail(new PublicError(bankAccountExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var bankAccountExternalId = bankAccountExternalIdResult.value();

        Currency currency;
        try
        {
            currency = Currency.valueOf(request.currency);
        } catch (IllegalArgumentException e)
        {
            return Result.fail(new PublicError(String.format("%s is not a valid Currency", request.currency), ErrorCode.invalid_currency));
        }

        var amountResult = Amount.createForTransaction(request.amount);
        if (amountResult.isFailed()) return Result.fail(new PublicError(amountResult.getFirstErrorMessage(), ErrorCode.invalid_amount));
        var amount = amountResult.value();

        Tag tag = null;
        if (request.tag != null)
        {
            var tagResult = Tag.create(request.tag);
            if (tagResult.isFailed()) return Result.fail(new PublicError(tagResult.getFirstErrorMessage(), ErrorCode.param_error));

            tag = tagResult.value();
        }

        var clientResult = clientAggregateRootRepository.getByExternalId(clientExternalId);
        if (clientResult.isFailed()) return Result.fail(new PublicError(clientResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var client = clientResult.value();
        if (client.getStatus() != ClientStatus.ACTIVE) return Result.fail(new PublicError(String.format("Client %s is in status %s : not authorized to credit money", client.getExternalId().value(), client.getStatus().toString().toLowerCase()), ErrorCode.client_error));

        var bankAccountResult = bankAccountAggregateRootRepository.getByExternalId(bankAccountExternalId);
        if (bankAccountResult.isFailed()) return Result.fail(new PublicError(bankAccountResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var bankAccount = bankAccountResult.value();

        if (!bankAccount.getClientExternalId().equals(clientExternalId))
            return Result.fail(new PublicError(String.format("Client %s is not the owner of the %s bank account", clientExternalId.value(), bankAccountExternalId.value()), ErrorCode.client_is_not_bank_account_owner));

        var creditTransactionResult = AccountingTransactionAggregate.create(
            AccountingTransactionAggregateId.create(),
            AccountingTransactionAggregateExternalId.create(),
            clientExternalId,
            bankAccountExternalId,
            amount,
            currency,
            TransactionType.CREDIT,
            tag);
        if (creditTransactionResult.isFailed()) return Result.fail(new PublicError(creditTransactionResult.getFirstErrorMessage(), ErrorCode.transaction_error));
        var creditTransaction = creditTransactionResult.value();

        Action action = () -> {
            var saveAfterCreation = accountingTransactionAggregateRootRepository.saveAggregate(creditTransaction);
            if (saveAfterCreation.isFailed()) return fail(saveAfterCreation.getFirstErrorMessage());

            var creditMoneyResult = bankAccount.creditMoney(creditTransaction.getAmount());
            if (creditMoneyResult.isFailed()) return fail(creditMoneyResult.getFirstErrorMessage());
            var saveBankAccountAfterCreditResult = bankAccountAggregateRootRepository.saveAggregate(bankAccount);
            if (saveBankAccountAfterCreditResult.isFailed()) return fail(saveBankAccountAfterCreditResult.getFirstErrorMessage());

            var succeedTransactionResult = creditTransaction.succeed();
            if (succeedTransactionResult.isFailed()) return fail(succeedTransactionResult.getFirstErrorMessage());
            var saveAfterSucceedResult = accountingTransactionAggregateRootRepository.saveAggregate(creditTransaction);
            if (saveAfterSucceedResult.isFailed()) return fail(saveAfterSucceedResult.getFirstErrorMessage());

            return ok();
        };
        var transactionResult = unitOfWork.executeTransaction(action);
        if (transactionResult.isFailed()) return Result.fail(new PublicError(transactionResult.getFirstErrorMessage(), ErrorCode.transaction_error));

        return Result.ok(buildCommandResponse(creditTransaction, OperationResult.Success));
    }

    private CreditCommandResponse buildCommandResponse(AccountingTransactionAggregate transactionAggregate, OperationResult operationResult)
    {
        return new CreditCommandResponse(
            transactionAggregate.getExternalId().value(),
            transactionAggregate.getBankAccountExternalId().value(),
            operationResult.code(),
            operationResult.message());
    }
}
