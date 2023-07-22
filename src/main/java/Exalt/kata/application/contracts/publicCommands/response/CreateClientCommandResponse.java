package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.contracts.AddressDTO;
import exalt.kata.application.primitives.IPublicCommandResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateClientCommandResponse(
    UUID id,
    LocalDateTime creationDate,
    String lastName,
    String firstName,
    AddressDTO address,
    LocalDate birthDate,
    String status) implements IPublicCommandResponse
{
}
