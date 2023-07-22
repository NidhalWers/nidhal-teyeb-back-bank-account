package exalt.kata.domain.accounting;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.AggregateRoot;
import exalt.kata.domain.core.primitives.results.base.Result;

import java.time.LocalDateTime;

public class AccountingTransactionAggregate extends AggregateRoot<AccountingTransactionAggregateId>
{
    private AccountingTransactionAggregateExternalId externalId;
    private ClientAggregateExternalId clientAggregateExternalId;
    private BankAccountAggregateExternalId bankAccountAggregateExternalId;
    private LocalDateTime creationDate;
    private LocalDateTime executionDate;
    private TransactionStatus status;
    private Amount amount;
    private Currency currency;
    private TransactionType type;
    private Tag tag;
    private OperationResult operationResult;

    private AccountingTransactionAggregate(
        AccountingTransactionAggregateId id,
        AccountingTransactionAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        BankAccountAggregateExternalId bankAccountAggregateExternalId,
        LocalDateTime creationDate,
        LocalDateTime executionDate,
        TransactionStatus status,
        Amount amount,
        Currency currency,
        TransactionType type,
        Tag tag,
        OperationResult operationResult)
    {
        super(id);
        this.externalId = externalId;
        this.clientAggregateExternalId = clientAggregateExternalId;
        this.bankAccountAggregateExternalId = bankAccountAggregateExternalId;
        this.creationDate = creationDate;
        this.executionDate = executionDate;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.tag = tag;
        this.operationResult = operationResult;
    }

    public static Result<AccountingTransactionAggregate> create(
        AccountingTransactionAggregateId id,
        AccountingTransactionAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        BankAccountAggregateExternalId bankAccountAggregateExternalId,
        Amount amount,
        Currency currency,
        TransactionType type,
        Tag tag)
    {
        return Result.ok(new AccountingTransactionAggregate(
            id,
            externalId,
            clientAggregateExternalId,
            bankAccountAggregateExternalId,
            LocalDateTime.now(),
            null,
            TransactionStatus.CREATED,
            amount,
            currency,
            type,
            tag,
            null));
    }

    public static AccountingTransactionAggregate createFromDatabase(
        AccountingTransactionAggregateId id,
        AccountingTransactionAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        BankAccountAggregateExternalId bankAccountAggregateExternalId,
        LocalDateTime creationDate,
        LocalDateTime executionDate,
        TransactionStatus status,
        Amount amount,
        Currency currency,
        TransactionType type,
        Tag tag,
        OperationResult operationResult)
    {
        return new AccountingTransactionAggregate(
            id,
            externalId,
            clientAggregateExternalId,
            bankAccountAggregateExternalId,
            creationDate,
            executionDate,
            status,
            amount,
            currency,
            type,
            tag,
            operationResult);
    }

    public AccountingTransactionAggregateExternalId getExternalId()
    {
        return externalId;
    }

    public ClientAggregateExternalId getClientExternalId()
    {
        return clientAggregateExternalId;
    }

    public BankAccountAggregateExternalId getBankAccountExternalId()
    {
        return bankAccountAggregateExternalId;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public LocalDateTime getExecutionDate()
    {
        return executionDate;
    }

    public TransactionStatus getStatus()
    {
        return status;
    }

    public Amount getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public TransactionType getType()
    {
        return type;
    }

    public Tag getTag()
    {
        return tag;
    }

    public OperationResult getOperationResult()
    {
        return operationResult;
    }

    public Result fail(OperationResult operationResult)
    {
        if (status == TransactionStatus.FAILED || status == TransactionStatus.SUCCEEDED)
            return Result.failed(String.format("Transaction %s already processed", id.value()));

        this.operationResult = operationResult;
        status = TransactionStatus.FAILED;

        return Result.ok();
    }

    public Result succeed()
    {
        if (status == TransactionStatus.FAILED || status == TransactionStatus.SUCCEEDED)
            return Result.failed(String.format("Transaction %s already processed", id.value()));

        this.operationResult = OperationResult.Success;
        status = TransactionStatus.SUCCEEDED;
        executionDate = LocalDateTime.now();

        return Result.ok();
    }
}
