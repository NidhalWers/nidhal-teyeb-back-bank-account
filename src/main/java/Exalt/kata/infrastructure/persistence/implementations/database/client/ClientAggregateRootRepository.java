package exalt.kata.infrastructure.persistence.implementations.database.client;

import exalt.kata.domain.Address;
import exalt.kata.domain.CountryCode;
import exalt.kata.domain.client.*;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class ClientAggregateRootRepository implements IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId>
{
    private IClientTableRepository dbContext;

    public ClientAggregateRootRepository(IClientTableRepository repository)
    {
        dbContext = repository;
    }

    public Result<ClientAggregate> getById(ClientAggregateId id)
    {
        var clientTable = getEntityById(id);
        if (clientTable.getId() == null) return Result.fail("The resource does not exist");

        var clientAggregate = mapTableToAggregate(clientTable);
        return Result.ok(clientAggregate);
    }

    public Result saveAggregate(ClientAggregate clientAggregate)
    {
        ClientTable clientTable = getEntityById(clientAggregate.getId());
        clientTable = mapAggregateToTable(clientAggregate, clientTable);
        try
        {
            dbContext.saveAndFlush(clientTable);
            return Result.ok();
        }
        catch (Exception e)
        {
            return Result.fail(e.getMessage());
        }
    }

    public Result<ClientAggregate> getByExternalId(ClientAggregateExternalId externalId)
    {
        var clientTable = dbContext.findAll().stream()
            .filter(x -> x.getExternalId().equals(externalId.value()))
            .findFirst();
        if (!clientTable.isPresent()) return Result.fail(String.format("client %s not found", externalId.value()));

        var clientAggregate = mapTableToAggregate(clientTable.get());
        return Result.ok(clientAggregate);
    }

    private ClientTable getEntityById(ClientAggregateId id)
    {
        var clientTableResult = dbContext.findAll().stream()
            .filter(x -> x.getId().equals(id.value()))
            .findFirst();
        return clientTableResult.orElse(null);
    }

    private ClientTable mapAggregateToTable(ClientAggregate clientAggregate, ClientTable clientTable)
    {
        if (clientTable == null)
        {
            clientTable = new ClientTable()
                .setId(clientAggregate.getId().value())
                .setExternalId(clientAggregate.getExternalId().value())
                .setCreationDate(clientAggregate.getCreationDate())
                .setLastName(clientAggregate.getLastName())
                .setFirstName(clientAggregate.getFirstName())
                .setBirthDate(clientAggregate.getBirthDate());
        }

        var addressTable = new AddressTable()
            .setAddressLine1(clientAggregate.getAddress().getAddressLine1())
            .setAddressLine2(clientAggregate.getAddress().getAddressLine2())
            .setCity(clientAggregate.getAddress().getCity())
            .setRegion(clientAggregate.getAddress().getRegion())
            .setPostalCode(clientAggregate.getAddress().getPostalCode())
            .setCountryCode(clientAggregate.getAddress().getCountryCode().name());

        clientTable.setAddress(addressTable).setStatus(clientAggregate.getStatus().name());
        if (clientAggregate.getIdentificationNumber() != null) clientTable.setIdentificationNumber(clientAggregate.getIdentificationNumber().value());
        if (clientAggregate.getNationality() != null) clientTable.setNationality(clientAggregate.getNationality().name());
        if (clientAggregate.getEmail() != null) clientTable.setEmail(clientAggregate.getEmail().value());
        if (clientAggregate.getPhoneNumber() != null) clientTable.setPhoneNumber(clientAggregate.getPhoneNumber().value());
        if (clientAggregate.getOccupation() != null) clientTable.setOccupation(clientAggregate.getOccupation().value());
        clientTable.setLastModificationDate(LocalDateTime.now());

        return clientTable;
    }

    private ClientAggregate mapTableToAggregate(ClientTable clientTable)
    {
        CountryCode nationality = null;
        if (clientTable.getNationality() != null) nationality = CountryCode.valueOf(clientTable.getNationality());

        var addressCountryCode = CountryCode.valueOf(clientTable.getAddress().getCountryCode());
        var address = Address.createFromDatabase(
            clientTable.getAddress().getAddressLine1(),
            clientTable.getAddress().getAddressLine2(),
            clientTable.getAddress().getCity(),
            clientTable.getAddress().getRegion(),
            clientTable.getAddress().getPostalCode(),
            addressCountryCode);

        var clientStatus = ClientStatus.valueOf(clientTable.getStatus());

        return ClientAggregate.createFromDatabase(
            ClientAggregateId.createFromDatabase(clientTable.getId()),
            ClientAggregateExternalId.createFromDatabase(clientTable.getExternalId()),
            clientTable.getCreationDate(),
            clientTable.getLastName(),
            clientTable.getFirstName(),
            address,
            clientTable.getBirthDate(),
            clientStatus,
            IdentificationNumber.createFromDatabase(clientTable.getIdentificationNumber()),
            nationality,
            Email.createFromDatabase(clientTable.getEmail()),
            PhoneNumber.createFromDatabase(clientTable.getPhoneNumber()),
            Occupation.createFromDatabase(clientTable.getOccupation()));
    }
}
