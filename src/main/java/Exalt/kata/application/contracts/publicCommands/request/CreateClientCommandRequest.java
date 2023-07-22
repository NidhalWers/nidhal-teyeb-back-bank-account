package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.contracts.AddressDTO;
import exalt.kata.application.primitives.IPublicCommandRequest;

import java.time.LocalDate;

public class CreateClientCommandRequest implements IPublicCommandRequest
{
    public String lastName;
    public String firstName;
    public LocalDate birthDate;
    public AddressDTO address;

    public CreateClientCommandRequest(
        String lastName,
        String firstName,
        LocalDate birthDate,
        AddressDTO address)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.address = address;
    }
}