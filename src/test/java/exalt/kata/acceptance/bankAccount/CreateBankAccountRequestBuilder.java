package exalt.kata.acceptance.bankAccount;

import exalt.kata.application.contracts.publicCommands.request.CreateBankAccountCommandRequest;

import java.util.UUID;

public class CreateBankAccountRequestBuilder
{
    public static CreateBankAccountRequestBuilder builder()
    {
        return new CreateBankAccountRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String currency = "EUR";
    private String tag = "RegisterKYCInformationCommandHandlerTest";
    private long authorizedOverdrawn = 0L;

    public CreateBankAccountRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public CreateBankAccountRequestBuilder withCurrency(String currency)
    {
        this.currency = currency;
        return this;
    }

    public CreateBankAccountRequestBuilder withTag(String tag)
    {
        this.tag = tag;
        return this;
    }

    public CreateBankAccountRequestBuilder withAuthorizedOverdrawn(long authorizedOverdrawn)
    {
        this.authorizedOverdrawn = authorizedOverdrawn;
        return this;
    }

    public CreateBankAccountCommandRequest build()
    {
        return new CreateBankAccountCommandRequest(clientExternalId, currency, tag, authorizedOverdrawn);
    }
}
