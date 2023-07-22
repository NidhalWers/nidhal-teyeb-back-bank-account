package exalt.kata.acceptance.client;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicCommands.request.RegisterKYCInformationCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.RegisterKYCInformationCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.kyc.contracts.IClientAuthorizationProxy;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static exalt.kata.common.ClientAggregateRootRepositoryMock.*;
import static exalt.kata.common.ClientAuthorizationProxyMock.mockIsClientAuthorizedFailed;
import static exalt.kata.common.ClientAuthorizationProxyMock.mockIsClientAuthorizedSuccess;
import static org.assertj.core.api.Assertions.assertThat;

public class RegisterKYCInformationCommandHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    @Mock
    IClientAuthorizationProxy clientAuthorizationProxy;
    @Autowired
    IPublicCommandHandler<RegisterKYCInformationCommandRequest, RegisterKYCInformationCommandResponse, PublicError> registerKYCInformationCommandHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(registerKYCInformationCommandHandler, "clientAggregateRootRepository", clientAggregateRootRepository);
        ReflectionTestUtils.setField(registerKYCInformationCommandHandler, "clientAuthorizationProxy", clientAuthorizationProxy);
        mockSaveAggregateWithSuccess(clientAggregateRootRepository);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withClientExternalId(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutIdentificationNumber_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withIdentificationNumber(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The identificationNumber field is required");
    }

    @Test
    public void withoutNationality_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withNationality(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The nationality field is required");
    }

    @Test
    public void withoutEmail_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withEmail(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The email field is required");
    }

    @Test
    public void withoutPhoneNumber_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withPhoneNumber(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The phoneNumber field is required");
    }

    @Test
    public void withoutOccupation_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withOccupation(null)
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The occupation field is required");
    }

    @Test
    public void withInvalidUUIDClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withClientExternalId("ThisIsAnInvalidClientExternalId")
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The field clientExternalId is in the wrong format");
    }

    @Test
    public void withInvalidNationality_ShouldBeFailed_AndReturnsInvalidCountryCode()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withNationality("XYZ")
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.invalid_country_code);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("XYZ is not a valid CountryCode");
    }

    @Test
    public void withInvalidEmail_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withEmail("thisIsAnInvalidEmail")
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Invalid email format");
    }

    @Test
    public void withInvalidPhoneNumber_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder()
            .withPhoneNumber("thisIsAnInvalidPhoneNumber")
            .build();

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Invalid phoneNumber format");
    }

    @Test
    public void withNotExistingClient_ShouldBeFailed_AndReturnsResourceNotFound()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithNotFound(clientAggregateRootRepository);

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("client not found");
    }

    @Test
    public void withNotAuthorizedClient_ShouldBeSucceeded_AndReturnsBlockedClient()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockIsClientAuthorizedFailed(clientAuthorizationProxy);

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().status()).isEqualTo(ClientStatus.BLOCKED.name());
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.AuthorizationKyCError.code());
    }

    @Test
    public void withAuthorizedClient_ShouldBeSucceeded_AndReturnsActivatedClient()
    {
        // Arrange
        var commandRequest = RegisterKYCInformationCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockIsClientAuthorizedSuccess(clientAuthorizationProxy);

        // Act
        var commandResponseResult = registerKYCInformationCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().status()).isEqualTo(ClientStatus.ACTIVE.name());
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.Success.code());
    }
}
