package exalt.kata.acceptance.bankAccount;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicCommands.request.CreateBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateBankAccountCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static exalt.kata.common.ClientAggregateRootRepositoryMock.mockGetClientByExternalIdWithNotFound;
import static exalt.kata.common.ClientAggregateRootRepositoryMock.mockGetClientByExternalIdWithSuccess;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBankAccountTableCommandHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    @Autowired
    IPublicCommandHandler<CreateBankAccountCommandRequest, CreateBankAccountCommandResponse, PublicError> createBankAccountCommandHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(createBankAccountCommandHandler, "clientAggregateRootRepository", clientAggregateRootRepository);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder()
            .withClientExternalId(null)
            .build();

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutCCurrency_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder()
            .withCurrency(null)
            .build();

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The currency field is required");
    }

    @Test
    public void withInvalidCurrency_ShouldBeFailed_AndReturnsInvalidCurrency()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder()
            .withCurrency("XYZ")
            .build();

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.invalid_currency);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("XYZ is not a valid Currency");
    }

    @Test
    public void withInvalidTag_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder()
            .withTag(String.join("", Collections.nCopies(256, "*")))
            .build();

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The field Tag must match the regular expression '^.{0,255}$'.");
    }

    @Test
    public void withNotExistingClient_ShouldBeFailed_AndResourceNotFound()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder().build();
        mockGetClientByExternalIdWithNotFound(clientAggregateRootRepository);

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("client not found");
    }

    @Test
    public void withNominalCase_ShouldBeSucceeded_WithActiveBankAccount()
    {
        // Arrange
        var commandRequest = CreateBankAccountRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);

        // Act
        var commandResponseResult = createBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().status()).isEqualTo(BankAccountStatus.ACTIVE.name());
        assertThat(commandResponseResult.value().amount()).isEqualTo(0L);
    }
}
