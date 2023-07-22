package exalt.kata.application.implementations.publicQueries;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicQueries.request.GetClientAccountsQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetClientAccountsQueryResponse;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import org.springframework.stereotype.Component;

@Component
public class GetClientAccountsQueryHandler implements IPublicQueryHandler<GetClientAccountsQueryRequest, GetClientAccountsQueryResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";

    private IBankAccountQueryAdapter bankAccountQueryAdapter;

    public GetClientAccountsQueryHandler(IBankAccountQueryAdapter bankAccountQueryAdapter)
    {
        this.bankAccountQueryAdapter = bankAccountQueryAdapter;
    }

    public Result<GetClientAccountsQueryResponse, PublicError> handleQuery(GetClientAccountsQueryRequest request)
    {
        if (request.clientExternalId() == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));

        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId());
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));
        var clientExternalId = clientExternalIdResult.value();

        var getBankAccountsByClientExternalId = bankAccountQueryAdapter.getNotDeletedBankAccountByClientExternalId(clientExternalId);

        return Result.ok(new GetClientAccountsQueryResponse(
            getBankAccountsByClientExternalId.stream()
                .map(x -> x.value().toString())
                .toList()
        ));
    }
}
