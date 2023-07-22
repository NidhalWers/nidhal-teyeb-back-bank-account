package exalt.kata.infrastructure.persistence.implementations.database.accounting;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.Tag;
import exalt.kata.domain.accounting.*;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AccountingTransactionAggregateRootRepository implements IAggregateRootRepository<AccountingTransactionAggregate, AccountingTransactionAggregateId, AccountingTransactionAggregateExternalId>
{
    private IAccountingTransactionTableRepository dbContext;

    public AccountingTransactionAggregateRootRepository(IAccountingTransactionTableRepository context)
    {
        dbContext = context;
    }

    public Result<AccountingTransactionAggregate> getById(AccountingTransactionAggregateId id)
    {
        var accountingTransactionTable = getEntityById(id);
        if (accountingTransactionTable.getId() == null) return Result.fail("The resource does not exist");

        var accountingTransactionAggregate = mapTableToAggregate(accountingTransactionTable);
        return Result.ok(accountingTransactionAggregate);
    }

    public Result saveAggregate(AccountingTransactionAggregate accountingTransactionAggregate)
    {
        AccountingTransactionTable accountingTransactionTable = getEntityById(accountingTransactionAggregate.getId());
        accountingTransactionTable = mapAggregateToTable(accountingTransactionAggregate, accountingTransactionTable);
        try
        {
            dbContext.saveAndFlush(accountingTransactionTable);
            return Result.ok();
        }
        catch (Exception e)
        {
            return Result.fail(e.getMessage());
        }
    }

    public Result<AccountingTransactionAggregate> getByExternalId(AccountingTransactionAggregateExternalId externalId)
    {
        var accountingTransactionTable = dbContext.findAll().stream()
            .filter(x -> x.getExternalId().equals(externalId.value()))
            .findFirst();
        if (!accountingTransactionTable.isPresent()) return Result.fail(String.format("transaction %s not found", externalId.value()));

        var accountingTransactionAggregate = mapTableToAggregate(accountingTransactionTable.get());
        return Result.ok(accountingTransactionAggregate);
    }

    private AccountingTransactionTable getEntityById(AccountingTransactionAggregateId id)
    {
        var accountingTransactionTableResult = dbContext.findAll().stream()
            .filter(x -> x.getId().equals(id.value()))
            .findFirst();
        return accountingTransactionTableResult.orElse(null);
    }

    private AccountingTransactionTable mapAggregateToTable(AccountingTransactionAggregate accountingTransactionAggregate, AccountingTransactionTable accountingTransactionTable)
    {
        if (accountingTransactionTable == null)
        {
            accountingTransactionTable = new AccountingTransactionTable()
                .setId(accountingTransactionAggregate.getId().value())
                .setExternalId(accountingTransactionAggregate.getExternalId().value())
                .setClientExternalId(accountingTransactionAggregate.getClientExternalId().value())
                .setBankAccountExternalId(accountingTransactionAggregate.getBankAccountExternalId().value())
                .setCreationDate(accountingTransactionAggregate.getCreationDate());
        }

        accountingTransactionTable
            .setExecutionDate(accountingTransactionAggregate.getExecutionDate())
            .setStatus(accountingTransactionAggregate.getStatus().name())
            .setAmount(accountingTransactionAggregate.getAmount().value())
            .setCurrency(accountingTransactionAggregate.getCurrency().name())
            .setType(accountingTransactionAggregate.getType().name());
        if (accountingTransactionAggregate.getTag() != null) accountingTransactionTable.setTag(accountingTransactionAggregate.getTag().value());
        if (accountingTransactionAggregate.getOperationResult() != null)
        {
            accountingTransactionTable.setOperationCode(accountingTransactionAggregate.getOperationResult().code())
            .setOperationMessage(accountingTransactionAggregate.getOperationResult().message());
        }

        return accountingTransactionTable;
    }

    private AccountingTransactionAggregate mapTableToAggregate(AccountingTransactionTable accountingTransactionTable)
    {
        return AccountingTransactionAggregate.createFromDatabase(
            AccountingTransactionAggregateId.createFromDatabase(accountingTransactionTable.getId()),
            AccountingTransactionAggregateExternalId.createFromDatabase(accountingTransactionTable.getExternalId()),
            ClientAggregateExternalId.createFromDatabase(accountingTransactionTable.getClientExternalId()),
            BankAccountAggregateExternalId.createFromDatabase(accountingTransactionTable.getClientExternalId()),
            accountingTransactionTable.getCreationDate(),
            accountingTransactionTable.getExecutionDate(),
            TransactionStatus.valueOf(accountingTransactionTable.getStatus()),
            Amount.createFromDatabase(accountingTransactionTable.getAmount()),
            Currency.valueOf(accountingTransactionTable.getCurrency()),
            TransactionType.valueOf(accountingTransactionTable.getType()),
            Tag.createFromDatabase(accountingTransactionTable.getTag()),
            OperationResult.createFromDatabase(accountingTransactionTable.getOperationCode(), accountingTransactionTable.getOperationMessage()));
    }
}
