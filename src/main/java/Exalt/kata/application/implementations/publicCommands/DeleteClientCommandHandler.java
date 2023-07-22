package exalt.kata.application.implementations.publicCommands;

import exalt.kata.application.contracts.PublicErrorMessages;
import exalt.kata.application.contracts.publicCommands.request.DeleteClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.DeleteClientCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.OperationResult;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteClientCommandHandler implements IPublicCommandHandler<DeleteClientCommandRequest, DeleteClientCommandResponse, PublicError>
{
    private static final String ParameterClientExternalId = "clientExternalId";

    private final IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository;
    private final IBankAccountQueryAdapter bankAccountQueryAdapter;

    public DeleteClientCommandHandler(
        IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> clientAggregateRootRepository,
        IBankAccountQueryAdapter bankAccountQueryAdapter)
    {
        this.clientAggregateRootRepository = clientAggregateRootRepository;
        this.bankAccountQueryAdapter = bankAccountQueryAdapter;
    }

    /**
     * We don't want to delete the customer directly from the database,
     * but use status to express the fact that a customer has been deleted,
     * for legal reasons for example.
     * In a complete application, we'd have a batch that would delete all customers in Deleted status
     * for X amount of time (X being a retention time determined by the product and legal teams).
     */
    public Result<DeleteClientCommandResponse, PublicError> handleCommand(DeleteClientCommandRequest request)
    {
        if (request.clientExternalId == null)
            return Result.fail(new PublicError(PublicErrorMessages.ParameterIsRequiredMessage(ParameterClientExternalId), ErrorCode.param_error));
        var clientExternalIdResult = ClientAggregateExternalId.create(request.clientExternalId);
        if (clientExternalIdResult.isFailed()) return Result.fail(new PublicError(clientExternalIdResult.getFirstErrorMessage(), ErrorCode.param_error));

        var clientAggregateResult = clientAggregateRootRepository.getByExternalId(clientExternalIdResult.value());
        if (clientAggregateResult.isFailed()) return Result.fail(new PublicError(clientAggregateResult.getFirstErrorMessage(), ErrorCode.resource_not_found));
        var clientAggregate = clientAggregateResult.value();

        var getBankAccountsByClientExternalId = bankAccountQueryAdapter.getNotDeletedBankAccountByClientExternalId(clientAggregate.getExternalId());
        if (!getBankAccountsByClientExternalId.isEmpty()) return Result.fail(new PublicError("Client have not deleted all his accounts", ErrorCode.client_error));

        var deleteClientResult = clientAggregate.delete();
        if (deleteClientResult.isFailed()) return Result.fail(new PublicError(deleteClientResult.getFirstErrorMessage(), ErrorCode.client_error));
        var saveClientDeletion = clientAggregateRootRepository.saveAggregate(clientAggregate);
        if (saveClientDeletion.isFailed()) return Result.fail(new PublicError(saveClientDeletion.getFirstErrorMessage(), ErrorCode.internal));

        return Result.ok(new DeleteClientCommandResponse(OperationResult.Success.code(), OperationResult.Success.message()));
    }
}
