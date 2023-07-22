package exalt.kata.application.contracts.publicQueries.response;

import exalt.kata.application.primitives.IPublicQueryResponse;

import java.util.List;

public record GetAccountTransactionsQueryResponse(
  List<GetAccountTransactionQueryResponse> transactions) implements IPublicQueryResponse
{
}
