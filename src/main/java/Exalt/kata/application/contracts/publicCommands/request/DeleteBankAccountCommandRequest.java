package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.primitives.IPublicCommandRequest;

public class DeleteBankAccountCommandRequest implements IPublicCommandRequest
{
    public String clientExternalId;
    public String bankAccountExternalId;

    public DeleteBankAccountCommandRequest(String clientExternalId, String bankAccountExternalId)
    {
        this.clientExternalId = clientExternalId;
        this.bankAccountExternalId = bankAccountExternalId;
    }
}
