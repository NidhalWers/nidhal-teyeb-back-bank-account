package exalt.kata.acceptance.client;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicCommands.request.DeleteClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.DeleteClientCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static exalt.kata.common.BankAccountQueryAdapterMock.mockGetNotDeletedBankAccountByClientExternalId;
import static exalt.kata.common.ClientAggregateRootRepositoryMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteClientCommandHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    @Mock
    IBankAccountQueryAdapter bankAccountQueryAdapter;
    @Autowired
    IPublicCommandHandler<DeleteClientCommandRequest, DeleteClientCommandResponse, PublicError> deleteClientCommandHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(deleteClientCommandHandler, "clientAggregateRootRepository", clientAggregateRootRepository);
        ReflectionTestUtils.setField(deleteClientCommandHandler, "bankAccountQueryAdapter", bankAccountQueryAdapter);
        mockSaveAggregateWithSuccess(clientAggregateRootRepository);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParameterRequired()
    {
        // Arrange
        var commandRequest = new DeleteClientCommandRequest(null);

        // Act
        var commandResponseResult = deleteClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withNotExistingClient_ShouldBeFailed_AndReturnsResourceNotFound()
    {
        // Arrange
        var commandRequest = new DeleteClientCommandRequest(UUID.randomUUID().toString());
        mockGetClientByExternalIdWithNotFound(clientAggregateRootRepository);

        // Act
        var commandResponseResult = deleteClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("client not found");
    }

    @Test
    public void withNotDeletedAccounts_ShouldBeFailed_AndReturnsClientHaveNotDeletedAllAccounts()
    {
        // Arrange
        var commandRequest = new DeleteClientCommandRequest(UUID.randomUUID().toString());
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            ClientStatus.ACTIVE);
        mockGetNotDeletedBankAccountByClientExternalId(bankAccountQueryAdapter, 4);

        // Act
        var commandResponseResult = deleteClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Client have not deleted all his accounts");
    }

    @Test
    public void withAlreadyDeletedClient_ShouldBeFailed_AndReturnsClientHaveNotDeletedAllAccounts()
    {
        // Arrange
        var commandRequest = new DeleteClientCommandRequest(UUID.randomUUID().toString());
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            ClientStatus.DELETED);
        mockGetNotDeletedBankAccountByClientExternalId(bankAccountQueryAdapter, 0);

        // Act
        var commandResponseResult = deleteClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.client_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Client already deleted");
    }

    @Test
    public void nominalCase_ShouldBeSucceeded()
    {
        // Arrange
        var commandRequest = new DeleteClientCommandRequest(UUID.randomUUID().toString());
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            ClientStatus.ACTIVE);
        mockGetNotDeletedBankAccountByClientExternalId(bankAccountQueryAdapter, 0);

        // Act
        var commandResponseResult = deleteClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.Success.code());
        assertThat(commandResponseResult.value().resultMessage()).isEqualTo(OperationResult.Success.message());
    }
}
