package exalt.kata.infrastructure.persistence.implementations.database.client;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ClientTable
{
    @Id
    private UUID id;
    private UUID externalId;
    private LocalDateTime creationDate;
    private String lastName;
    private String firstName;
    @OneToOne(cascade = CascadeType.ALL)
    private AddressTable address;
    private LocalDate birthDate;
    private String status;
    private String identificationNumber;
    private String nationality;
    private String email;
    private String phoneNumber;
    private String occupation;
    private LocalDateTime lastModificationDate;

    public ClientTable()
    {
    }

    public UUID getId()
    {
        return id;
    }

    public ClientTable setId(UUID id)
    {
        this.id = id;
        return this;
    }

    public UUID getExternalId()
    {
        return externalId;
    }

    public ClientTable setExternalId(UUID externalId)
    {
        this.externalId = externalId;
        return this;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public ClientTable setCreationDate(LocalDateTime creationDate)
    {
        this.creationDate = creationDate;
        return this;
    }

    public String getLastName()
    {
        return lastName;
    }

    public ClientTable setLastName(String lastName)
    {
        this.lastName = lastName;
        return this;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public ClientTable setFirstName(String firstName)
    {
        this.firstName = firstName;
        return this;
    }

    public AddressTable getAddress()
    {
        return address;
    }

    public ClientTable setAddress(AddressTable address)
    {
        this.address = address;
        return this;
    }

    public LocalDate getBirthDate()
    {
        return birthDate;
    }

    public ClientTable setBirthDate(LocalDate birthDate)
    {
        this.birthDate = birthDate;
        return this;
    }

    public String getStatus()
    {
        return status;
    }

    public ClientTable setStatus(String status)
    {
        this.status = status;
        return this;
    }

    public String getIdentificationNumber()
    {
        return identificationNumber;
    }

    public ClientTable setIdentificationNumber(String identificationNumber)
    {
        this.identificationNumber = identificationNumber;
        return this;
    }

    public String getNationality()
    {
        return nationality;
    }

    public ClientTable setNationality(String nationality)
    {
        this.nationality = nationality;
        return this;
    }

    public String getEmail()
    {
        return email;
    }

    public ClientTable setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public ClientTable setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public ClientTable setOccupation(String occupation)
    {
        this.occupation = occupation;
        return this;
    }

    public LocalDateTime getLastModificationDate()
    {
        return lastModificationDate;
    }

    public ClientTable setLastModificationDate(LocalDateTime lastModificationDate)
    {
        this.lastModificationDate = lastModificationDate;
        return this;
    }
}
