package exalt.kata.application.contracts.publicQueries.response;

import exalt.kata.application.primitives.IPublicQueryResponse;

import java.util.List;

public record GetClientAccountsQueryResponse
    (
        List<String> bankAccountsIds
    ) implements IPublicQueryResponse
{
}
