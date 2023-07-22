package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.primitives.IPublicCommandRequest;

public class DebitCommandRequest implements IPublicCommandRequest
{
    public String clientExternalId;
    public String bankAccountId;
    public long amount;
    public String currency;
    public String tag;

    public DebitCommandRequest(String clientExternalId, String bankAccountId, long amount, String currency, String tag)
    {
        this.clientExternalId = clientExternalId;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.currency = currency;
        this.tag = tag;
    }
}
