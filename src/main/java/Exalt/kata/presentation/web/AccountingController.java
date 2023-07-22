package exalt.kata.presentation.web;

import exalt.kata.application.contracts.publicCommands.request.CreditCommandRequest;
import exalt.kata.application.contracts.publicCommands.request.DebitCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreditCommandResponse;
import exalt.kata.application.contracts.publicCommands.response.DebitCommandResponse;
import exalt.kata.application.contracts.publicQueries.request.GetAccountTransactionsQueryRequest;
import exalt.kata.application.contracts.publicQueries.response.GetAccountTransactionsQueryResponse;
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
public class AccountingController
{
    private static final String DateTimePattern  = "yyyy-MM-dd'T'HH:mm:ss";

    private IPublicCommandHandler<CreditCommandRequest, CreditCommandResponse, PublicError> creditCommandHandler;
    private IPublicCommandHandler<DebitCommandRequest, DebitCommandResponse, PublicError> debitCommandHandler;
    private IPublicQueryHandler<GetAccountTransactionsQueryRequest, GetAccountTransactionsQueryResponse, PublicError> getAccountTransactionQueryHandler;


    public AccountingController(
        IPublicCommandHandler<CreditCommandRequest, CreditCommandResponse, PublicError> handler,
        IPublicCommandHandler<DebitCommandRequest, DebitCommandResponse, PublicError> response,
        IPublicQueryHandler<GetAccountTransactionsQueryRequest, GetAccountTransactionsQueryResponse, PublicError> queryHandler)
    {
        creditCommandHandler = handler;
        debitCommandHandler = response;
        getAccountTransactionQueryHandler = queryHandler;
    }

    @PostMapping(value = "/{clientId}/credit",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public CreditCommandResponse credit(@PathVariable String clientId, @RequestBody CreditCommandRequest request){
        request.clientExternalId = clientId;
        var commandResponse = creditCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }

    @PostMapping(value = "/{clientId}/debit",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public DebitCommandResponse debit(@PathVariable String clientId, @RequestBody DebitCommandRequest request){
        request.clientExternalId = clientId;
        var commandResponse = debitCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }


    @GetMapping("/{clientId}/bankAccount/{bankAccountId}/transactions")
    @DateTimeFormat(pattern = DateTimePattern)
    public GetAccountTransactionsQueryResponse listTransactions(@PathVariable String clientId, @PathVariable String bankAccountId, Integer numberOfDays){
        var request = new GetAccountTransactionsQueryRequest(clientId, bankAccountId, numberOfDays);
        var queryResponse = getAccountTransactionQueryHandler.handleQuery(request);
        if (queryResponse.isFailed()) throw new ApplicationException(queryResponse.publicError());

        return queryResponse.value();
    }
}
