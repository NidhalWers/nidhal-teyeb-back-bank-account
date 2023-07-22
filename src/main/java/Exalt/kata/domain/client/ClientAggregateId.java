package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.UUID;

public class ClientAggregateId extends SimpleValueObject<UUID>
{
    private ClientAggregateId(UUID value)
    {
        super(value);
    }

    public static ClientAggregateId create()
    {
        return new ClientAggregateId(UUID.randomUUID());
    }

    public static ClientAggregateId createFromDatabase(UUID id)
    {
        return new ClientAggregateId(id);
    }
}
