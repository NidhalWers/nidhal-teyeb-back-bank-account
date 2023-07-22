package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class ClientAggregateExternalId extends SimpleValueObject<UUID>
{
    private ClientAggregateExternalId(UUID value)
    {
        super(value);
    }

    public static ClientAggregateExternalId create()
    {
        return new ClientAggregateExternalId(UUID.randomUUID());
    }

    public static Result<ClientAggregateExternalId> create(String value)
    {
        try
        {
            var uuid = UUID.fromString(value);
            return Result.ok(new ClientAggregateExternalId(uuid));
        } catch (IllegalArgumentException e)
        {
            return Result.fail("The field clientExternalId is in the wrong format");
        }
    }

    public static ClientAggregateExternalId createFromDatabase(UUID externalId)
    {
        return new ClientAggregateExternalId(externalId);
    }
}
