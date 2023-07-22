package exalt.kata.application.contracts.publicQueries.response;

import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.application.primitives.IPublicQueryResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetAccountTransactionQueryResponse(
    UUID bankAccountExternalId,
    String type,
    long amount,
    String currency,
    LocalDateTime executionDate,
    String tag) implements IPublicQueryResponse
{
}
