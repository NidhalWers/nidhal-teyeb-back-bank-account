package exalt.kata.domain.bankAccount;

import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class BankAccountAggregateId extends SimpleValueObject<UUID>
{
    private BankAccountAggregateId(UUID value)
    {
        super(value);
    }

    public static BankAccountAggregateId create()
    {
        return new BankAccountAggregateId(UUID.randomUUID());
    }

    public static BankAccountAggregateId createFromDatabase(UUID id)
    {
        return new BankAccountAggregateId(id);
    }
}