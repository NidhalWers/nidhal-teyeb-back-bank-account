package exalt.kata.application.contracts.publicQueries.response;

import exalt.kata.application.primitives.IPublicQueryResponse;

import java.util.UUID;

public record GetAccountBalanceQueryResponse(
    UUID bankAccountExternalId,
    long amount,
    String currency,
    String status,
    String tag) implements IPublicQueryResponse
{
}
