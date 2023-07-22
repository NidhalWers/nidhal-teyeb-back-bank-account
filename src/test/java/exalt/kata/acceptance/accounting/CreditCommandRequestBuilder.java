package exalt.kata.acceptance.accounting;

import exalt.kata.application.contracts.publicCommands.request.CreditCommandRequest;

import java.util.UUID;

public class CreditCommandRequestBuilder
{
    public static CreditCommandRequestBuilder builder()
    {
        return new CreditCommandRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String bankAccountExternalId = UUID.randomUUID().toString();
    private long amount = 100;
    private String currency = "EUR";
    private String tag = "RegisterKYCInformationCommandHandlerTest";

    public CreditCommandRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public CreditCommandRequestBuilder withBankAccountExternalId(String bankAccountExternalId)
    {
        this.bankAccountExternalId = bankAccountExternalId;
        return this;
    }

    public CreditCommandRequestBuilder withAmount(long amount)
    {
        this.amount = amount;
        return this;
    }

    public CreditCommandRequestBuilder withCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public CreditCommandRequestBuilder withTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public CreditCommandRequest build()
    {
        return new CreditCommandRequest(
            clientExternalId,
            bankAccountExternalId,
            amount,
            currency,
            tag);
    }
}
