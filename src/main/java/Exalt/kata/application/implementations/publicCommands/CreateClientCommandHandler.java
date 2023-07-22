package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.AddressDTO;
import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.CreateClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateClientCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.Address;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateClientCommandHandler implements IPublicCommandHandler<CreateClientCommandRequest, CreateClientCommandResponse, PublicError>
{
    private static final String ParameterLastName = "lastName";
    private static final String ParameterFirstName = "firstName";
    private static final String ParameterBirthDate = "birthDate";
    private static final String ParameterAddress = "address";
    private final IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;

    public CreateClientCommandHandler(IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository)
    {
        this.clientAggregateRootRepository = clientAggregateRootRepository;
    }

    public Result<CreateClientCommandResponse, PublicError> handleCommand(CreateClientCommandRequest request)
    {
        if (request.lastName == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterLastName), ErrorCode.param_error));
        if (request.firstName == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterFirstName), ErrorCode.param_error));
        if (request.birthDate == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterBirthDate), ErrorCode.param_error));
        if (request.address == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterAddress), ErrorCode.param_error));

        var addressResult = Address.create(
            request.address.addressLine1(),
            request.address.addressLine2(),
            request.address.city(),
            request.address.region(),
            request.address.postalCode(),
            request.address.countryCode());
        if (addressResult.isFailed()) return Result.fail(new PublicError(addressResult.getFirstErrorMessage(), ErrorCode.param_error));

        var clientAggregateResult = ClientAggregate.create(
            ClientAggregateId.create(),
            ClientAggregateExternalId.create(),
            request.lastName,
            request.firstName,
            addressResult.value(),
            request.birthDate);
        if (clientAggregateResult.isFailed()) return Result.fail(new PublicError(clientAggregateResult.getFirstErrorMessage(), ErrorCode.client_error));
        var clientAggregate = clientAggregateResult.value();
        var saveAfterCreationResult = clientAggregateRootRepository.saveAggregate(clientAggregate);
        if (saveAfterCreationResult.isFailed()) return Result.fail(new PublicError(saveAfterCreationResult.getFirstErrorMessage(), ErrorCode.internal));

        return Result.ok(buildCommandResponse(clientAggregate));
    }

    private CreateClientCommandResponse buildCommandResponse(ClientAggregate clientAggregate)
    {
        var addressDTO = new AddressDTO(
            clientAggregate.getAddress().getAddressLine1(),
            clientAggregate.getAddress().getAddressLine2(),
            clientAggregate.getAddress().getCity(),
            clientAggregate.getAddress().getRegion(),
            clientAggregate.getAddress().getPostalCode(),
            clientAggregate.getAddress().getCountryCode().name());

        return new CreateClientCommandResponse(
            clientAggregate.getExternalId().value(),
            clientAggregate.getCreationDate(),
            clientAggregate.getLastName(),
            clientAggregate.getFirstName(),
            addressDTO,
            clientAggregate.getBirthDate(),
            clientAggregate.getStatus().name());
    }
}
