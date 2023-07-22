package exalt.kata.application.implementations.publicQueries;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicQueries.request.GetAccountBalanceQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountBalanceQueryResponse;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import org.springframework.stereotype.Component;

@Component
public class GetAccountBalanceQueryHandler implements IPublicQueryHandler<GetAccountBalanceQueryRequest, GetAccountBalanceQueryResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";
    private static final String ParameterBankAccountExternalId = "bankAccountId";
    private final IBankAccountQueryAdapter bankAccountQueryAdapter;

    public GetAccountBalanceQueryHandler(IBankAccountQueryAdapter adapter) {bankAccountQueryAdapter = adapter;}

    public Result<GetAccountBalanceQueryResponse, PublicError> handleQuery(GetAccountBalanceQueryRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        if (request.bankAccountExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterBankAccountExternalId), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var clientExternalId = clientExternalIdResult.value();

        var bankAccountExternalIdResult = BankAccountAggregateExternalId.create(request.bankAccountExternalId);
        if (bankAccountExternalIdResult.isFailed()) return Result.fail(new PublicError(bankAccountExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var bankAccountExternalId = bankAccountExternalIdResult.value();

        var getBankAccountByExternalIdResult = bankAccountQueryAdapter.getByExternalId(clientExternalId, bankAccountExternalId);
        if (getBankAccountByExternalIdResult.isFailed()) return Result.fail(new PublicError(getBankAccountByExternalIdResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var getBankAccountByExternalId = getBankAccountByExternalIdResult.value();

        return Result.ok(
            new GetAccountBalanceQueryResponse(
                getBankAccountByExternalId.externalId().value(),
                getBankAccountByExternalId.amount().value(),
                getBankAccountByExternalId.currency().name(),
                getBankAccountByExternalId.status().name(),
                getBankAccountByExternalId.tag().value())
        );
    }
}
