package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.primitives.IPublicCommandResponse;

public record DeleteBankAccountCommandResponse(
    String resultCode,
    String resultMessage) implements IPublicCommandResponse
{
}
