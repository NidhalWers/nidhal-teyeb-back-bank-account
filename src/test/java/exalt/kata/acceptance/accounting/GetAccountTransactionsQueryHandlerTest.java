package exalt.kata.acceptance.accounting;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.publicQueries.request.GetAccountTransactionsQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountTransactionsQueryResponse;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.infrastructure.persistence.contracts.accounting.IAccountingQueryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static exalt.kata.common.AccountingQueryAdapterMock.mockQueryGetListOfTransaction;
import static org.assertj.core.api.Assertions.assertThat;

public class GetAccountTransactionsQueryHandlerTest extends AcceptanceTestBase
{
    @Mock
    IAccountingQueryAdapter accountingQueryAdapter;
    @Autowired
    IPublicQueryHandler<GetAccountTransactionsQueryRequest, GetAccountTransactionsQueryResponse, PublicError> getAccountTransactionsQueryHandler;

    @BeforeEach
    public void setup()
    {
        ReflectionTestUtils.setField(getAccountTransactionsQueryHandler, "accountingQueryAdapter", accountingQueryAdapter);
    }

    @Test
    public void withoutClientExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var queryRequest = GetAccountTransactionsQueryRequestBuilder.builder()
            .withClientExternalId(null)
            .build();

        // Act
        var queryResponseResult = getAccountTransactionsQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isFailed()).isTrue();
        assertThat(queryResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(queryResponseResult.getFirstErrorMessage()).isEqualTo("The clientExternalId field is required");
    }

    @Test
    public void withoutBankAccountExternalId_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var queryRequest = GetAccountTransactionsQueryRequestBuilder.builder()
            .withBankAccountExternalId(null)
            .build();

        // Act
        var queryResponseResult = getAccountTransactionsQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isFailed()).isTrue();
        assertThat(queryResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(queryResponseResult.getFirstErrorMessage()).isEqualTo("The bankAccountId field is required");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3, 5, 15})
    public void withZeroTransactions_ShouldBeSucceeded(int numberOfTransactions)
    {
        // Arrange
        var queryRequest = GetAccountTransactionsQueryRequestBuilder.builder().build();
        mockQueryGetListOfTransaction(accountingQueryAdapter, numberOfTransactions);

        // Act
        var queryResponseResult = getAccountTransactionsQueryHandler.handleQuery(queryRequest);

        // Assert
        assertThat(queryResponseResult.isSuccess()).isTrue();
        assertThat(queryResponseResult.value().transactions().size()).isEqualTo(numberOfTransactions);
    }
}
