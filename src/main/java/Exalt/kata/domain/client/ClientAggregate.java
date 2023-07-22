package exalt.kata.domain.client;

import exalt.kata.domain.Address;
import exalt.kata.domain.CountryCode;
import exalt.kata.domain.core.primitives.AggregateRoot;
import exalt.kata.domain.core.primitives.results.base.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientAggregate extends AggregateRoot<ClientAggregateId>
{
    private static final int MinimumAgeToBeClient = 16;
    private ClientAggregateExternalId externalId;
    private LocalDateTime creationDate;
    private String lastName;
    private String firstName;
    private Address address;
    private LocalDate birthDate;
    private ClientStatus status;
    private IdentificationNumber identificationNumber;
    private CountryCode nationality;
    private Email email;
    private PhoneNumber phoneNumber;
    private Occupation occupation;

    public ClientAggregate(
        ClientAggregateId id,
        ClientAggregateExternalId externalId,
        LocalDateTime creationDate,
        String lastName,
        String firstName,
        Address address,
        LocalDate birthDate,
        ClientStatus status,
        IdentificationNumber identificationNumber,
        CountryCode nationality,
        Email email,
        PhoneNumber phoneNumber,
        Occupation occupation)
    {
        super(id);
        this.externalId = externalId;
        this.creationDate = creationDate;
        this.lastName = lastName;
        this.firstName = firstName;
        this.address = address;
        this.birthDate = birthDate;
        this.status = status;
        this.identificationNumber = identificationNumber;
        this.nationality = nationality;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.occupation = occupation;
    }

    public static Result<ClientAggregate> create(
        ClientAggregateId id,
        ClientAggregateExternalId externalId,
        String lastName,
        String firstName,
        Address address,
        LocalDate birthDate)
    {
        if (birthDate.isAfter(LocalDate.now().minusYears(MinimumAgeToBeClient)))
            return Result.fail(String.format("Client must be at least %d years old", MinimumAgeToBeClient));

        return Result.ok(new ClientAggregate(
            id,
            externalId,
            LocalDateTime.now(),
            lastName,
            firstName,
            address,
            birthDate,
            ClientStatus.CREATED,
            null,
            null,
            null,
            null,
            null));
    }

    public static ClientAggregate createFromDatabase(
        ClientAggregateId id,
        ClientAggregateExternalId externalId,
        LocalDateTime creationDate,
        String lastName,
        String firstName,
        Address address,
        LocalDate birthDate,
        ClientStatus status,
        IdentificationNumber identificationNumber,
        CountryCode nationality,
        Email email,
        PhoneNumber phoneNumber,
        Occupation occupation)
    {
        return new ClientAggregate(
            id,
            externalId,
            creationDate,
            lastName,
            firstName,
            address,
            birthDate,
            status,
            identificationNumber,
            nationality,
            email,
            phoneNumber,
            occupation);
    }

    public ClientAggregateExternalId getExternalId()
    {
        return externalId;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public Address getAddress()
    {
        return address;
    }

    public LocalDate getBirthDate()
    {
        return birthDate;
    }

    public ClientStatus getStatus()
    {
        return status;
    }

    public IdentificationNumber getIdentificationNumber()
    {
        return identificationNumber;
    }

    public CountryCode getNationality()
    {
        return nationality;
    }

    public Email getEmail()
    {
        return email;
    }

    public PhoneNumber getPhoneNumber()
    {
        return phoneNumber;
    }

    public Occupation getOccupation()
    {
        return occupation;
    }

    public Result activate(
        IdentificationNumber identificationNumber,
        CountryCode countryCode,
        Email email,
        PhoneNumber phoneNumber,
        Occupation occupation)
    {
        this.identificationNumber = identificationNumber;
        this.nationality = countryCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.occupation = occupation;

        this.status = ClientStatus.ACTIVE;

        return Result.ok();
    }
    public Result block()
    {
        this.status = ClientStatus.BLOCKED;
        return Result.ok();
    }
    public Result delete()
    {
        if (status == ClientStatus.DELETED)
            return Result.fail("Client already deleted");

        this.status = ClientStatus.DELETED;
        return Result.ok();
    }
}
