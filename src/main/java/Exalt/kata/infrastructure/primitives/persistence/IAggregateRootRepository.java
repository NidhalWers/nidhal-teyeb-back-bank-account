package exalt.kata.infrastructure.primitives.persistence;

import exalt.kata.domain.core.primitives.AggregateRoot;
import exalt.kata.domain.core.primitives.Result;

public interface IAggregateRootRepository<TAggregateRoot extends AggregateRoot<TAggregateRootId>, TAggregateRootId, TAggregateRootExternalId>
{
    Result<TAggregateRoot> getById(TAggregateRootId id);

    Result saveAggregate(TAggregateRoot aggregateRoot);

    Result<TAggregateRoot> getByExternalId(TAggregateRootExternalId externalId);
}

