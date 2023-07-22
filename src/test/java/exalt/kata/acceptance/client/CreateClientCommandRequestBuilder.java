package exalt.kata.acceptance.client;

import exalt.kata.application.contracts.AddressDTO;
import exalt.kata.application.contracts.publicCommands.request.CreateClientCommandRequest;

import java.time.LocalDate;

public class CreateClientCommandRequestBuilder
{
    public static CreateClientCommandRequestBuilder builder()
    {
        return new CreateClientCommandRequestBuilder();
    }

    private String lastName = "toto";
    private String firstName = "riri";
    private LocalDate birthDate = LocalDate.of(1985, 06, 23);
    private AddressDTO address = new AddressDTO("addresseLine1", "addresseLine2", "Paris", "IDF", "75009", "FR");

    public CreateClientCommandRequestBuilder withLastName(String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public CreateClientCommandRequestBuilder withFirstName(String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public CreateClientCommandRequestBuilder withBirthDate(LocalDate birthDate)
    {
        this.birthDate = birthDate;
        return this;
    }

    public CreateClientCommandRequestBuilder withAddress(AddressDTO address)
    {
        this.address = address;
        return this;
    }

    public CreateClientCommandRequest build()
    {
        return new CreateClientCommandRequest(lastName, firstName, birthDate, address);
    }
}
