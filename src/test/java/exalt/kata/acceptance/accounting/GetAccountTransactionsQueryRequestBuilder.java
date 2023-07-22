package exalt.kata.acceptance.accounting;

import exalt.kata.application.contracts.publicQueries.request.GetAccountTransactionsQueryRequest;

import java.util.UUID;

public class GetAccountTransactionsQueryRequestBuilder
{
    public static GetAccountTransactionsQueryRequestBuilder builder(){
        return new GetAccountTransactionsQueryRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String bankAccountExternalId = UUID.randomUUID().toString();
    private int numberOfDays = 1;

    public GetAccountTransactionsQueryRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public GetAccountTransactionsQueryRequestBuilder withBankAccountExternalId(String bankAccountExternalId)
    {
        this.bankAccountExternalId = bankAccountExternalId;
        return this;
    }

    public GetAccountTransactionsQueryRequestBuilder withNumberOfDays(int numberOfDays)
    {
        this.numberOfDays = numberOfDays;
        return this;
    }

    public GetAccountTransactionsQueryRequest build(){
        return new GetAccountTransactionsQueryRequest(clientExternalId, bankAccountExternalId, numberOfDays);
    }
}
