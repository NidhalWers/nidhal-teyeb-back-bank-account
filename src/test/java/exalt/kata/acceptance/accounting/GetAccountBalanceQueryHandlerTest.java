package exalt.kata.acceptance.accounting;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicQueries.request.GetAccountBalanceQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountBalanceQueryResponse;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static exalt.kata.common.BankAccountQueryAdapterMock.mockQueryGetBankAccountByExternalIdWithNotFound;
import static exalt.kata.common.BankAccountQueryAdapterMock.mockQueryGetBankAccountByExternalIdWithSucceess;
import static org.assertj.core.api.Assertions.assertThat;

public class GetAccountBalanceQueryHandlerTest extends AcceptanceTestBase
{
    @Mock
    IBankAccountQueryAdapter bankAccountQueryAdapter;
    @Autowired
    IPublicQueryHandler<GetAccountBalanceQueryRequest, GetAccountBalanceQueryResponse, PublicError> getAccountBalanceQueryHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(getAccountBalanceQueryHandler, "bankAccountQueryAdapter", bankAccountQueryAdapter);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var queryRequest = GetAccountBalanceQueryRequestBuilder.builder()
            .withClientExternalId(null)
            .build();

        // Act
        var queryResponseResult = getAccountBalanceQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isFailed()).isTrue();
        assertThat(queryResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(queryResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutBankAccountExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var queryRequest = GetAccountBalanceQueryRequestBuilder.builder()
            .withBankAccountExternalId(null)
            .build();

        // Act
        var queryResponseResult = getAccountBalanceQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isFailed()).isTrue();
        assertThat(queryResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(queryResponseResult.getFirstErrorMessage()).isEqualTo("The bankAccountId field is required");
    }

    @Test
    public void withBankAccountNotFound_ShouldBeFailed_AndReturnsResourceNotFound()
    {
        // Arrange
        var queryRequest = GetAccountBalanceQueryRequestBuilder.builder().build();
        mockQueryGetBankAccountByExternalIdWithNotFound(bankAccountQueryAdapter);

        // Act
        var queryResponseResult = getAccountBalanceQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isFailed()).isTrue();
        assertThat(queryResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.resource_not_found);
        assertThat(queryResponseResult.getFirstErrorMessage()).isEqualTo("Bank account not found");
    }

    @Test
    public void withNominalCase_ShouldBeSucceeded()
    {
        // Arrange
        var queryRequest = GetAccountBalanceQueryRequestBuilder.builder().build();
        mockQueryGetBankAccountByExternalIdWithSucceess(bankAccountQueryAdapter, BankAccountAggregateExternalId.createFromDatabase(UUID.fromString(queryRequest.bankAccountExternalId)), 14500L);

        // Act
        var queryResponseResult = getAccountBalanceQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isSuccess()).isTrue();
        assertThat(queryResponseResult.value().bankAccountExternalId().toString()).isEqualTo(queryRequest.bankAccountExternalId);
        assertThat(queryResponseResult.value().amount()).isEqualTo(14500L);
    }
}
