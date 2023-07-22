package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.primitives.IPublicCommandRequest;

public class DeleteClientCommandRequest implements IPublicCommandRequest
{
    public String clientExternalId;

    public DeleteClientCommandRequest(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
    }
}
