package exalt.kata.presentation.web;

import exalt.kata.application.contracts.publicCommands.request.DeleteClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.request.RegisterKYCInformationCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.DeleteClientCommandResponse;
import exalt.kata.application.contracts.publicCommands.response.RegisterKYCInformationCommandResponse;
import exalt.kata.application.contracts.publicCommands.request.CreateClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateClientCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.presentation.web.error.ApplicationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v0.1",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientsController
{
    private static final String DateTimePattern  = "yyyy-MM-dd";
    private final IPublicCommandHandler<CreateClientCommandRequest, CreateClientCommandResponse, PublicError> createClientCommandHandler;
    private final IPublicCommandHandler<RegisterKYCInformationCommandRequest, RegisterKYCInformationCommandResponse, PublicError> registerKycInformationCommandHandler;
    private final IPublicCommandHandler<DeleteClientCommandRequest, DeleteClientCommandResponse, PublicError> deleteClientCommandHandler;


    public ClientsController(
        IPublicCommandHandler<CreateClientCommandRequest, CreateClientCommandResponse, PublicError> handler,
        IPublicCommandHandler<RegisterKYCInformationCommandRequest, RegisterKYCInformationCommandResponse, PublicError> commandHandler, IPublicCommandHandler<DeleteClientCommandRequest, DeleteClientCommandResponse, PublicError> clientCommandHandler)
    {
        createClientCommandHandler = handler;
        registerKycInformationCommandHandler = commandHandler;
        deleteClientCommandHandler = clientCommandHandler;
    }


    @PostMapping("/clients")
    public CreateClientCommandResponse createClient(@RequestBody @DateTimeFormat(pattern = DateTimePattern) CreateClientCommandRequest request){
        var commandResponse = createClientCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }

    @PostMapping("/{clientId}/register")
    public RegisterKYCInformationCommandResponse registerClient(@PathVariable String clientId, @RequestBody @DateTimeFormat(pattern = DateTimePattern) RegisterKYCInformationCommandRequest request){
        request.clientExternalId = clientId;
        var commandResponse = registerKycInformationCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }

    @DeleteMapping("/{clientId}")
    public DeleteClientCommandResponse deleteClient(@PathVariable String clientId){
        var request = new DeleteClientCommandRequest(clientId);
        var commandResponse = deleteClientCommandHandler.handleCommand(request);
        if (commandResponse.isFailed()) throw new ApplicationException(commandResponse.publicError());

        return commandResponse.value();
    }
}
