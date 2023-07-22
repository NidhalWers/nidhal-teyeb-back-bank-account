package exalt.kata.acceptance.accounting;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicCommands.request.CreditCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreditCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
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

import static exalt.kata.common.BankAccountAggregateRootRepositoryMock.mockGetBankAccountByExternalIdWithSuccess;
import static exalt.kata.common.BankAccountAggregateRootRepositoryMock.mockSaveAggregateWithSuccess;
import static exalt.kata.common.ClientAggregateRootRepositoryMock.mockGetClientByExternalIdWithNotFound;
import static exalt.kata.common.ClientAggregateRootRepositoryMock.mockGetClientByExternalIdWithSuccess;
import static org.assertj.core.api.Assertions.assertThat;

public class CreditCommandHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    @Mock
    IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository;
    @Autowired
    IPublicCommandHandler<CreditCommandRequest, CreditCommandResponse, PublicError> creditCommandHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(creditCommandHandler, "clientAggregateRootRepository", clientAggregateRootRepository);
        ReflectionTestUtils.setField(creditCommandHandler, "bankAccountAggregateRootRepository", bankAccountAggregateRootRepository);
        mockSaveAggregateWithSuccess(bankAccountAggregateRootRepository);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder()
            .withClientExternalId(null)
            .build();

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutCCurrency_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder()
            .withCurrency(null)
            .build();

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The currency field is required");
    }

    @Test
    public void withInvalidCurrency_ShouldBeFailed_AndReturnsInvalidCurrency()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder()
            .withCurrency("XYZ")
            .build();

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.invalid_currency);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("XYZ is not a valid Currency");
    }

    @Test
    public void withInvalidTag_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder()
            .withTag(String.join("", Collections.nCopies(256, "*")))
            .build();

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The field Tag must match the regular expression '^.{0,255}$'.");
    }

    @Test
    public void withNegativeAmount_ShouldBeFailed_AndReturnsInvalidAmount()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder()
            .withAmount(-10L)
            .build();

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.invalid_amount);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Transaction amount cannot be null or negative");
    }

    @Test
    public void withNotExistingClient_ShouldBeFailed_AndReturnsResourceNotFound()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithNotFound(clientAggregateRootRepository);

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("client not found");
    }

    @Test
    public void withBlockedBankAccount_ShouldBeFailed_AndReturnsTransactionError()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            BankAccountAggregateExternalId.create(commandRequest.bankAccountId).value(),
            BankAccountStatus.BLOCKED,
            Currency.EUR,
            Amount.createForBalance(50000L),
            Amount.NullAmount);

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.transaction_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("To CreditMoney, the BankAccount status must be ACTIVE and not BLOCKED");
    }

    @Test
    public void withBankAccountUnauthorizedToCredit_ShouldBeFailed_AndReturnsTransactionError()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            BankAccountAggregateExternalId.create(commandRequest.bankAccountId).value(),
            BankAccountStatus.UNAUTHORIZED_TO_CREDIT,
            Currency.EUR,
            Amount.createForBalance(50000L),
            Amount.NullAmount);

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.transaction_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("To CreditMoney, the BankAccount status must be ACTIVE and not UNAUTHORIZED_TO_CREDIT");
    }

    @Test
    public void withBankAccountUnauthorizedToDebit_ShouldBeSucceeded()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            BankAccountAggregateExternalId.create(commandRequest.bankAccountId).value(),
            BankAccountStatus.UNAUTHORIZED_TO_DEBIT,
            Currency.EUR,
            Amount.createForBalance(50000L),
            Amount.NullAmount);

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.Success.code());
    }

    @Test
    public void withActiveBankAccount_ShouldBeSucceeded()
    {
        // Arrange
        var commandRequest = CreditCommandRequestBuilder.builder().build();
        mockGetClientByExternalIdWithSuccess(
            clientAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            ClientStatus.ACTIVE);
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.create(commandRequest.clientExternalId).value(),
            BankAccountAggregateExternalId.create(commandRequest.bankAccountId).value(),
            BankAccountStatus.ACTIVE,
            Currency.EUR,
            Amount.createForBalance(50000L),
            Amount.NullAmount);

        // Act
        var commandResponseResult = creditCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.Success.code());
    }
}
