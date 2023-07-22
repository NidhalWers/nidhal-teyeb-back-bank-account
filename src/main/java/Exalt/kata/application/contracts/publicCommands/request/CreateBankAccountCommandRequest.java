package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.primitives.IPublicCommandRequest;

public class CreateBankAccountCommandRequest implements IPublicCommandRequest
{
    public String clientExternalId;
    public String currency;
    public String tag;
    public long overdrawnAuthorized;

    public CreateBankAccountCommandRequest(
        String clientExternalId,
        String currency,
        String tag,
        long overdrawnAuthorized)
    {
        this.clientExternalId = clientExternalId;
        this.currency = currency;
        this.tag = tag;
        this.overdrawnAuthorized = overdrawnAuthorized;
    }
}
