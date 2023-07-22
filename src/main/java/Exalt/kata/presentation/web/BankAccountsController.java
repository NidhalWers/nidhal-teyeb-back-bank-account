package exalt.kata.presentation.web;

import exalt.kata.application.contracts.publicCommands.request.CreateBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.request.DeleteBankAccountCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateBankAccountCommandResponse;
import exalt.kata.application.contracts.publicCommands.response.DeleteBankAccountCommandResponse;
import exalt.kata.application.contracts.publicQueries.request.GetAccountBalanceQueryRequest;
import exalt.kata.application.contracts.publicQueries.request.GetClientAccountsQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountBalanceQueryResponse;
import exalt.kata.application.contracts.publicQueries.response.GetClientAccountsQueryResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.application.primitives.IPublicQueryHandler;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.presentation.web.error.ApplicationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v0.1",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class BankAccountsController
{
    private static final String DateTimePattern  = "yyyy-MM-dd";

    private IPublicCommandHandler<CreateBankAccountCommandRequest, CreateBankAccountCommandResponse, PublicError> createBankAccountCommandHandler;
    private IPublicQueryHandler<GetAccountBalanceQueryRequest, GetAccountBalanceQueryResponse, PublicError> getAccountBalanceQueryHandler;
    private IPublicCommandHandler<DeleteBankAccountCommandRequest, DeleteBankAccountCommandResponse, PublicError> deleteBankAccountCommandHandler;
    private IPublicQueryHandler<GetClientAccountsQueryRequest, GetClientAccountsQueryResponse, PublicError> getClientAccountsQueryHandler;

    public BankAccountsController(
        IPublicCommandHandler<CreateBankAccountCommandRequest, CreateBankAccountCommandResponse, PublicError> handler,
        IPublicQueryHandler<GetAccountBalanceQueryRequest, GetAccountBalanceQueryResponse, PublicError> queryHandler,
        IPublicCommandHandler<DeleteBankAccountCommandRequest, DeleteBankAccountCommandResponse, PublicError> commandHandler,
        IPublicQueryHandler<GetClientAccountsQueryRequest, GetClientAccountsQueryResponse, PublicError> accountsQueryHandler)
    {
        createBankAccountCommandHandler = handler;
        getAccountBalanceQueryHandler = queryHandler;
        deleteBankAccountCommandHandler = commandHandler;
        getClientAccountsQueryHandler = accountsQueryHandler;
    }


    @PostMapping(value = "/{clientId}/bankAccount",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreateBankAccountCommandResponse createBankAccount(@PathVariable String clientId, @RequestBody @DateTimeFormat(pattern = DateTimePattern) CreateBankAccountCommandRequest request){
        request.clientExternalId = clientId;
        var commandResponse = createBankAccountCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }

    @GetMapping("/{clientId}/bankAccount/{bankAccountId}")
    public GetAccountBalanceQueryResponse getBalance(@PathVariable String clientId, @PathVariable String bankAccountId){
        var request = new GetAccountBalanceQueryRequest(clientId, bankAccountId);
        var queryResponse = getAccountBalanceQueryHandler.handleQuery(request);
        if (queryResponse.isFailed()) throw new ApplicationException(queryResponse.publicError());

        return queryResponse.value();
    }

    @DeleteMapping("/{clientId}/{bankAccountId}")
    public DeleteBankAccountCommandResponse deleteBankAccount(@PathVariable String clientId, @PathVariable String bankAccountId){
        var request = new DeleteBankAccountCommandRequest(clientId, bankAccountId);
        var commandResponse = deleteBankAccountCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }

    @GetMapping("/{clientId}/")
    public GetClientAccountsQueryResponse getBankAccounts(@PathVariable String clientId){
        var request = new GetClientAccountsQueryRequest(clientId);
        var queryResponse = getClientAccountsQueryHandler.handleQuery(request);
        if (queryResponse.isFailed()) throw new ApplicationException(queryResponse.publicError());

        return queryResponse.value();
    }
}
