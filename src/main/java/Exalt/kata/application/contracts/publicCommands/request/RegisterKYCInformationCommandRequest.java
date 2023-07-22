package exalt.kata.application.contracts.publicCommands.request;

import exalt.kata.application.primitives.IPublicCommandRequest;

public class RegisterKYCInformationCommandRequest implements IPublicCommandRequest
{
    public String clientExternalId;
    public String identificationNumber;
    public String nationality;
    public String email;
    public String phoneNumber;
    public String occupation;
    public String sourceOfFunds;

    public RegisterKYCInformationCommandRequest(
        String clientExternalId,
        String identificationNumber,
        String nationality,
        String email,
        String phoneNumber,
        String occupation,
        String sourceOfFunds)
    {
        this.clientExternalId = clientExternalId;
        this.identificationNumber = identificationNumber;
        this.nationality = nationality;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.occupation = occupation;
        this.sourceOfFunds = sourceOfFunds;
    }
}

