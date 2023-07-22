package exalt.kata.infrastructure.persistence.implementations.database.bankAccount;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class BankAccountAggregateRootRepository implements IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId>
{
    private IBankAccountTableRepository dbContext;

    public BankAccountAggregateRootRepository(IBankAccountTableRepository context)
    {
        dbContext = context;
    }

    public Result<BankAccountAggregate> getById(BankAccountAggregateId id)
    {
        var bankAccountTable = getEntityById(id);
        if (bankAccountTable.getId() == null) return Result.fail("The resource does not exist");

        var bankAccountAggregate = mapTableToAggregate(bankAccountTable);
        return Result.ok(bankAccountAggregate);
    }

    public Result saveAggregate(BankAccountAggregate bankAccountAggregate)
    {
        BankAccountTable bankAccountTable = getEntityById(bankAccountAggregate.getId());
        bankAccountTable = mapAggregateToTable(bankAccountAggregate, bankAccountTable);
        try
        {
            dbContext.saveAndFlush(bankAccountTable);
            return Result.ok();
        }
        catch (Exception e)
        {
            return Result.fail(e.getMessage());
        }
    }

    public Result<BankAccountAggregate> getByExternalId(BankAccountAggregateExternalId externalId)
    {
        var bankAccountTable = dbContext.findAll().stream()
            .filter(x -> x.getExternalId().equals(externalId.value()))
            .findFirst();
        if (!bankAccountTable.isPresent()) return Result.fail(String.format("bankAccount %s not found", externalId.value()));

        var bankAccountAggregate = mapTableToAggregate(bankAccountTable.get());
        return Result.ok(bankAccountAggregate);
    }

    private BankAccountTable getEntityById(BankAccountAggregateId id)
    {
        var bankAccountTableResult = dbContext.findAll().stream()
            .filter(x -> x.getId().equals(id.value()))
            .findFirst();
        return bankAccountTableResult.orElse(null);
    }

    private BankAccountTable mapAggregateToTable(BankAccountAggregate bankAccountAggregate, BankAccountTable bankAccountTable)
    {
        if (bankAccountTable == null)
        {
            bankAccountTable = new BankAccountTable()
                .setId(bankAccountAggregate.getId().value())
                .setExternalId(bankAccountAggregate.getExternalId().value())
                .setClientExternalId(bankAccountAggregate.getClientExternalId().value())
                .setCreationDate(bankAccountAggregate.getCreationDate());
        }

        bankAccountTable.setStatus(bankAccountAggregate.getStatus().name())
            .setAmount(bankAccountAggregate.getAmount().value())
            .setCurrency(bankAccountAggregate.getCurrency().name())
            .setOverdrawnAuthorized(bankAccountAggregate.getOverdrawnAuthorized().value());
        if (bankAccountAggregate.getTag() != null) bankAccountTable.setTag(bankAccountAggregate.getTag().value());

        bankAccountTable.setLastModificationDate(LocalDateTime.now());

        return bankAccountTable;
    }

    private BankAccountAggregate mapTableToAggregate(BankAccountTable bankAccountTable)
    {
        var bankAccountStatus = BankAccountStatus.valueOf(bankAccountTable.getStatus());
        var currency = Currency.valueOf(bankAccountTable.getCurrency());

        return BankAccountAggregate.createFromDatabase(
            BankAccountAggregateId.createFromDatabase(bankAccountTable.getId()),
            BankAccountAggregateExternalId.createFromDatabase(bankAccountTable.getExternalId()),
            ClientAggregateExternalId.createFromDatabase(bankAccountTable.getClientExternalId()),
            bankAccountTable.getCreationDate(),
            bankAccountStatus,
            currency,
            Amount.createFromDatabase(bankAccountTable.getAmount()),
            Tag.createFromDatabase(bankAccountTable.getTag()),
            Amount.createFromDatabase(bankAccountTable.getOverdrawnAuthorized()));
    }
}
