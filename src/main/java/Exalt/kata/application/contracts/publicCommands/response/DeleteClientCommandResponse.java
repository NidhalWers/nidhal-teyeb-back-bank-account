package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.primitives.IPublicCommandResponse;

public record DeleteClientCommandResponse(
    String resultCode,
    String resultMessage) implements IPublicCommandResponse
{
}
