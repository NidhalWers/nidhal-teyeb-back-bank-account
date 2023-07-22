package exalt.kata.domain.accounting;

import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class AccountingTransactionAggregateExternalId extends SimpleValueObject<UUID>
{
    private AccountingTransactionAggregateExternalId(UUID value)
    {
        super(value);
    }

    public static AccountingTransactionAggregateExternalId create()
    {
        return new AccountingTransactionAggregateExternalId(UUID.randomUUID());
    }

    public static AccountingTransactionAggregateExternalId createFromDatabase(UUID id)
    {
        return new AccountingTransactionAggregateExternalId(id);
    }
}
