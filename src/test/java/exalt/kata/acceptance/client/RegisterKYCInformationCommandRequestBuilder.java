package exalt.kata.acceptance.client;

import exalt.kata.application.contracts.publicCommands.request.RegisterKYCInformationCommandRequest;

import java.util.UUID;

public class RegisterKYCInformationCommandRequestBuilder
{
    public static RegisterKYCInformationCommandRequestBuilder builder()
    {
        return new RegisterKYCInformationCommandRequestBuilder();
    }

    private String clientExternalId = UUID.randomUUID().toString();
    private String identificationNumber = "12345N67M";
    private String nationality = "FR";
    private String email = "dev@exalt.com";
    private String phoneNumber = "+33712046535";
    private String occupation = "software engineer";
    private String sourceOfFunds = "salary";

    public RegisterKYCInformationCommandRequestBuilder withClientExternalId(String clientExternalId)
    {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withIdentificationNumber(String identificationNumber)
    {
        this.identificationNumber = identificationNumber;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withNationality(String nationality)
    {
        this.nationality = nationality;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withEmail(String email)
    {
        this.email = email;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withOccupation(String occupation)
    {
        this.occupation = occupation;
        return this;
    }

    public RegisterKYCInformationCommandRequestBuilder withSourceOfFunds(String sourceOfFunds)
    {
        this.sourceOfFunds = sourceOfFunds;
        return this;
    }

    public RegisterKYCInformationCommandRequest build()
    {
        return new RegisterKYCInformationCommandRequest(
            clientExternalId,
            identificationNumber,
            nationality,
            email,
            phoneNumber,
            occupation,
            sourceOfFunds);
    }
}
