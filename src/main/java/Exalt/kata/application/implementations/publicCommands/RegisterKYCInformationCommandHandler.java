package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.RegisterKYCInformationCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.RegisterKYCInformationCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.CountryCode;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.client.*;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.kyc.contracts.IClientAuthorizationProxy;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Component;

@Component
public class RegisterKYCInformationCommandHandler implements IPublicCommandHandler<RegisterKYCInformationCommandRequest, RegisterKYCInformationCommandResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterIdentificationNumber = "identificationNumber";
    private static final String ParameterNationality = "nationality";
    private static final String ParameterEmail = "email";
    private static final String ParameterPhoneNumber = "phoneNumber";
    private static final String ParameterOccupation = "occupation";
    private static final String ParameterSourceOfFunds = "sourceOfFunds";

    private final IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    private final IClientAuthorizationProxy clientAuthorizationProxy;

    public RegisterKYCInformationCommandHandler(
        IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository,
        IClientAuthorizationProxy clientAuthorizationProxy)
    {
        this.clientAggregateRootRepository = clientAggregateRootRepository;
        this.clientAuthorizationProxy = clientAuthorizationProxy;
    }

    public Result<RegisterKYCInformationCommandResponse, PublicError> handleCommand(RegisterKYCInformationCommandRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.identificationNumber == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterIdentificationNumber), ErrorCode.param_error));
        if (request.nationality == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterNationality), ErrorCode.param_error));
        if (request.email == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterEmail), ErrorCode.param_error));
        if (request.phoneNumber == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterPhoneNumber), ErrorCode.param_error));
        if (request.occupation == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterOccupation), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));

        var identificationNumber = IdentificationNumber.create(request.identificationNumber);

        CountryCode nationality;
        try
        {
            nationality = CountryCode.valueOf(request.nationality);
        } catch (IllegalArgumentException e)
        {
            return Result.fail(new PublicError(String.format("%s is not a valid CountryCode", request.nationality), ErrorCode.invalid_country_code));
        }

        var emailResult = Email.create(request.email);
        if (emailResult.isFailed()) return Result.fail(new PublicError(emailResult.getFirstErrorMessage(), ErrorCode.param_error));

        var phoneNumberResult = PhoneNumber.create(request.phoneNumber);
        if (phoneNumberResult.isFailed()) return Result.fail(new PublicError(phoneNumberResult.getFirstErrorMessage(), ErrorCode.param_error));

        var occupation = Occupation.create(request.occupation);

        var clientAggregateResult = clientAggregateRootRepository.getByExternalId(clientExternalIdResult.value());
        if (clientAggregateResult.isFailed()) return Result.fail(new PublicError(clientAggregateResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var clientAggregate = clientAggregateResult.value();

        var isClientAuthorized = clientAuthorizationProxy.isClientAuthorized(
            clientAggregate.getExternalId(),
            nationality,
            identificationNumber);
        if (isClientAuthorized.isFailed())
        {
            clientAggregate.block();
            var saveAfterBlock = clientAggregateRootRepository.saveAggregate(clientAggregate);
            if (saveAfterBlock.isFailed()) return Result.fail(new PublicError(saveAfterBlock.getFirstErrorMessage(), ErrorCode.internal));

            return Result.ok(buildCommandResponse(clientAggregate, OperationResult.AuthorizationKyCError));
        }

        var activateResult = clientAggregate.activate(
            identificationNumber,
            nationality,
            emailResult.value(),
            phoneNumberResult.value(),
            occupation);
        if (activateResult.isFailed()) return Result.fail(new PublicError(activateResult.getFirstErrorMessage(), ErrorCode.client_error));
        var saveAfterActivation = clientAggregateRootRepository.saveAggregate(clientAggregate);
        if (saveAfterActivation.isFailed()) return Result.fail(new PublicError(saveAfterActivation.getFirstErrorMessage(), ErrorCode.internal));

        return Result.ok(buildCommandResponse(clientAggregate, OperationResult.Success));
    }

    private RegisterKYCInformationCommandResponse buildCommandResponse(ClientAggregate clientAggregate, OperationResult operationResult)
    {
        return new RegisterKYCInformationCommandResponse(
            clientAggregate.getExternalId().value(),
            clientAggregate.getLastName(),
            clientAggregate.getFirstName(),
            clientAggregate.getBirthDate(),
            (clientAggregate.getIdentificationNumber() == null ? null : clientAggregate.getIdentificationNumber().value()),
            (clientAggregate.getNationality() == null ? null : clientAggregate.getNationality().name()),
            (clientAggregate.getEmail() == null ? null : clientAggregate.getEmail().value()),
            (clientAggregate.getPhoneNumber() == null ? null : clientAggregate.getPhoneNumber().value()),
            clientAggregate.getStatus().name(),
            operationResult.code(),
            operationResult.message());
    }
}
