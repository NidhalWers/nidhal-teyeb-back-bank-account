package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.primitives.IPublicCommandResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBankAccountCommandResponse(
    UUID id,
    UUID clientId,
    LocalDateTime creationDate,
    String status,
    long amount,
    String currency,
    String tag) implements IPublicCommandResponse
{
}
