package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.primitives.IPublicCommandResponse;

import java.util.UUID;

public record CreditCommandResponse(
    UUID id,
    UUID bankAccountId,
    String resultCode,
    String resultMessage) implements IPublicCommandResponse
{
}
