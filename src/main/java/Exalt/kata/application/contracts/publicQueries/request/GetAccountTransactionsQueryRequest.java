package exalt.kata.application.contracts.publicQueries.request;

import exalt.kata.application.primitives.IPublicQueryRequest;

public record GetAccountTransactionsQueryRequest(
    String clientExternalId,
    String bankAccountExternalId,
    Integer numberOfDays) implements IPublicQueryRequest
{
}
