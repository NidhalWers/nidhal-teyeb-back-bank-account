package exalt.kata.application.implementations.publicQueries;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicQueries.request.GetAccountTransactionsQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountTransactionQueryResponse;
import exalt.kata.application.contracts.publicQueries.response.GetAccountTransactionsQueryResponse;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.persistence.contracts.accounting.GetTransaction;
import exalt.kata.infrastructure.persistence.contracts.accounting.IAccountingQueryAdapter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetAccountTransactionsQueryHandler implements IPublicQueryHandler<GetAccountTransactionsQueryRequest, GetAccountTransactionsQueryResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterBankAccountExternalId = "bankAccountId";

    private final IAccountingQueryAdapter accountingQueryAdapter;

    public GetAccountTransactionsQueryHandler(IAccountingQueryAdapter adapter)
    {
        accountingQueryAdapter = adapter;
    }

    public Result<GetAccountTransactionsQueryResponse, PublicError> handleQuery(GetAccountTransactionsQueryRequest request)
    {
        if (request.clientExternalId() == null) return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.bankAccountExternalId() == null) return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterBankAccountExternalId), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId());
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var clientExternalId = clientExternalIdResult.value();

        var bankAccountExternalIdResult = BankAccountAggregateExternalId.create(request.bankAccountExternalId());
        if (bankAccountExternalIdResult.isFailed()) return Result.fail(new PublicError(bankAccountExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var bankAccountExternalId = bankAccountExternalIdResult.value();

        LocalDateTime getTransactionPeriod;
        if (request.numberOfDays() == null || request.numberOfDays() <= 0) getTransactionPeriod = LocalDateTime.now().minusDays(1).withHour(0).withMinute(1);
        else getTransactionPeriod = LocalDateTime.now().minusDays(request.numberOfDays()).withHour(0).withMinute(1);

        var getListOfTransaction = accountingQueryAdapter.getListOfTransaction(clientExternalId, bankAccountExternalId, getTransactionPeriod);

        return Result.ok(buildQueryResponse(getListOfTransaction));
    }

    public GetAccountTransactionsQueryResponse buildQueryResponse(List<GetTransaction> transactions) {
        var accountTransactions = transactions.stream()
            .map(this::mapToAccountTransactionResponse)
            .collect(Collectors.toList());

        return new GetAccountTransactionsQueryResponse(accountTransactions);
    }

    private GetAccountTransactionQueryResponse mapToAccountTransactionResponse(GetTransaction transaction) {
        return new GetAccountTransactionQueryResponse(
            transaction.bankAccountAggregateExternalId().value(),
            transaction.type().toString(),
            transaction.amount().value(),
            transaction.currency().toString(),
            transaction.executionDate(),
            transaction.tag().value()
        );
    }

}