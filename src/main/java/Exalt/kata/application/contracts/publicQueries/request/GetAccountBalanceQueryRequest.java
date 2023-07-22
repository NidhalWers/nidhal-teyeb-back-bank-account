package exalt.kata.application.contracts.publicQueries.request;

import exalt.kata.application.primitives.IPublicQueryRequest;

public class GetAccountBalanceQueryRequest implements IPublicQueryRequest
{
    public String clientExternalId;
    public String bankAccountExternalId;

    public GetAccountBalanceQueryRequest(String clientExternalId, String bankAccountExternalId)
    {
        this.clientExternalId = clientExternalId;
        this.bankAccountExternalId = bankAccountExternalId;
    }
}
