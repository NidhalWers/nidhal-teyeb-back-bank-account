package exalt.kata.domain.accounting;

import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class AccountingTransactionAggregateId extends SimpleValueObject<UUID>
{
    private AccountingTransactionAggregateId(UUID value)
    {
        super(value);
    }

    public static AccountingTransactionAggregateId create()
    {
        return new AccountingTransactionAggregateId(UUID.randomUUID());
    }

    public static AccountingTransactionAggregateId createFromDatabase(UUID id)
    {
        return new AccountingTransactionAggregateId(id);
    }
}
