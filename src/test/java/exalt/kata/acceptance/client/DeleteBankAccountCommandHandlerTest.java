package exalt.kata.acceptance.client;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicCommands.request.DeleteBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.DeleteBankAccountCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static exalt.kata.common.BankAccountAggregateRootRepositoryMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBankAccountCommandHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> bankAccountAggregateRootRepository;
    @Autowired
    IPublicCommandHandler<DeleteBankAccountCommandRequest, DeleteBankAccountCommandResponse, PublicError> deleteBankAccountCommandHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(deleteBankAccountCommandHandler, "bankAccountAggregateRootRepository", bankAccountAggregateRootRepository);
        mockSaveAggregateWithSuccess(bankAccountAggregateRootRepository);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(null, UUID.randomUUID().toString());

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutBankAccountExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), null);

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The bankAccountId field is required");
    }

    @Test
    public void withNotExistingBankAccount_ShouldBeFailed_AndReturnsResourceNotFound()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithNotFound(bankAccountAggregateRootRepository);

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("bankAccount not found");
    }

    @Test
    public void withClientNotBankAccountOwner_ShouldBeFailed_AndReturnsClientIsNotBankAccountOwner()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.randomUUID()),
            BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.bankAccountExternalId)),
            BankAccountStatus.ACTIVE,
            Currency.EUR,
            Amount.NullAmount,
            Amount.createFromDatabase(150L));

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.client_is_not_bank_account_owner);
    }

    @Test
    public void withAlreadyDeletedBankAccount_ShouldBeFailed_AndReturnsBankAccountError()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.bankAccountExternalId)),
            BankAccountStatus.DELETED,
            Currency.EUR,
            Amount.NullAmount,
            Amount.createFromDatabase(150L));

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.bank_account_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Bank account already deleted");
    }

    @Test
    public void withBalanceGreaterThanZero_ShouldBeFailed_AndReturnsBankAccountError()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.bankAccountExternalId)),
            BankAccountStatus.ACTIVE,
            Currency.EUR,
            Amount.createFromDatabase(2000L),
            Amount.createFromDatabase(150L));

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.bank_account_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Bank account can not be deleted with a balance of 2000");
    }

    @Test
    public void withBalanceLesserThanZero_ShouldBeFailed_AndReturnsBankAccountError()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.bankAccountExternalId)),
            BankAccountStatus.ACTIVE,
            Currency.EUR,
            Amount.createFromDatabase(-50L),
            Amount.createFromDatabase(150L));

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.bank_account_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Bank account can not be deleted with a balance of -50");
    }

    @Test
    public void NominalCase_ShouldBeSucceeded()
    {
        // Arrange
        var commandRequest = new DeleteBankAccountCommandRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        mockGetBankAccountByExternalIdWithSuccess(
            bankAccountAggregateRootRepository,
            ClientAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.clientExternalId)),
            BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(commandRequest.bankAccountExternalId)),
            BankAccountStatus.ACTIVE,
            Currency.EUR,
            Amount.NullAmount,
            Amount.createFromDatabase(150L));

        // Act
        var commandResponseResult = deleteBankAccountCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value().resultCode()).isEqualTo(OperationResult.Success.code());
        assertThat(commandResponseResult.value().resultMessage()).isEqualTo(OperationResult.Success.message());
    }
}
