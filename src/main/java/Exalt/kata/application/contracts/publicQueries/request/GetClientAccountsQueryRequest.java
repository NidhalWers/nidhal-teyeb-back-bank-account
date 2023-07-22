package exalt.kata.application.contracts.publicQueries.request;

import exalt.kata.application.primitives.IPublicQueryRequest;

public record GetClientAccountsQueryRequest
    (
        String clientExternalId
    ) implements IPublicQueryRequest
{
}
