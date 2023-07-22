package exalt.kata.domain.bankAccount;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class BankAccountAggregateExternalId extends SimpleValueObject<UUID>
{
    private BankAccountAggregateExternalId(UUID value)
    {
        super(value);
    }

    public static BankAccountAggregateExternalId create()
    {
        return new BankAccountAggregateExternalId(UUID.randomUUID());
    }

    public static BankAccountAggregateExternalId createFromDatabase(UUID uuid)
    {
        return new BankAccountAggregateExternalId(uuid);
    }

    public static Result<BankAccountAggregateExternalId> create(String value)
    {
        try
        {
            var uuid = UUID.fromString(value);
            return Result.ok(new BankAccountAggregateExternalId(uuid));
        } catch (IllegalArgumentException e)
        {
            return Result.fail("The field bankAccountExternalId is in the wrong format");
        }
    }
}