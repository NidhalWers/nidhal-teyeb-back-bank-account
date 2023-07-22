package exalt.kata.acceptance.accounting;

import exalt.kata.application.contracts.publicCommands.request.DebitCommandRequest;

import java.util.UUID;

public class DebitCommandRequestBuilder
{
    public static DebitCommandRequestBuilder builder()
    {
        return new DebitCommandRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String bankAccountExternalId = UUID.randomUUID().toString();
    private long amount = 100;
    private String currency = "EUR";
    private String tag = "RegisterKYCInformationCommandHandlerTest";

    public DebitCommandRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public DebitCommandRequestBuilder withBankAccountExternalId(String bankAccountExternalId)
    {
        this.bankAccountExternalId = bankAccountExternalId;
        return this;
    }

    public DebitCommandRequestBuilder withAmount(long amount)
    {
        this.amount = amount;
        return this;
    }

    public DebitCommandRequestBuilder withCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public DebitCommandRequestBuilder withTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public DebitCommandRequest build()
    {
        return new DebitCommandRequest(
            clientExternalId,
            bankAccountExternalId,
            amount,
            currency,
            tag);
    }
}
