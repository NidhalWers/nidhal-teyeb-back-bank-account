package exalt.kata.application.contracts.publicCommands.response;

import exalt.kata.application.primitives.IPublicCommandResponse;

import java.time.LocalDate;
import java.util.UUID;

public record RegisterKYCInformationCommandResponse(
    UUID id,
    String lastName,
    String firstName,
    LocalDate birthDate,
    String identificationNumber,
    String nationality,
    String email,
    String phoneNumber,
    String status,
    String resultCode,
    String resultMessage) implements IPublicCommandResponse
{
}
