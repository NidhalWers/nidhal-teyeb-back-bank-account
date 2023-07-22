package exalt.kata.acceptance.accounting;

import exalt.kata.application.contracts.publicQueries.request.GetAccountBalanceQueryRequest;

import java.util.UUID;

public class GetAccountBalanceQueryRequestBuilder
{
    public static GetAccountBalanceQueryRequestBuilder builder()
    {
        return new GetAccountBalanceQueryRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String bankAccountExternalId = UUID.randomUUID().toString();

    public GetAccountBalanceQueryRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public GetAccountBalanceQueryRequestBuilder withBankAccountExternalId(String bankAccountExternalId)
    {
        this.bankAccountExternalId = bankAccountExternalId;
        return this;
    }

    public GetAccountBalanceQueryRequest build()
    {
        return new GetAccountBalanceQueryRequest(clientExternalId, bankAccountExternalId);
    }
}
