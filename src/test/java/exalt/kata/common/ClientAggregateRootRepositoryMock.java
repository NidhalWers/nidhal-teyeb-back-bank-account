package exalt.kata.common;

import exalt.kata.domain.Address;
import exalt.kata.domain.client.ClientAggregate;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateId;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientAggregateRootRepositoryMock
{
    public static void mockSaveAggregateWithSuccess(IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> aggregateRootRepository)
    {
        when(aggregateRootRepository.saveAggregate(
            any(ClientAggregate.class)))
            .thenReturn(Result.ok());
    }

    public static void mockGetClientByExternalIdWithNotFound(IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> aggregateRootRepository)
    {
        when(aggregateRootRepository.getByExternalId(any(ClientAggregateExternalId.class)))
            .thenReturn(Result.fail("client not found"));
    }

    public static void mockGetClientByExternalIdWithSuccess(
        IAggregateRootRepository<ClientAggregate, ClientAggregateId, ClientAggregateExternalId> aggregateRootRepository,
        ClientAggregateExternalId clientAggregateExternalId,
        ClientStatus status)
    {
        var clientAggregate = ClientAggregate.createFromDatabase(
            ClientAggregateId.create(),
            clientAggregateExternalId,
            LocalDateTime.now().minusDays(5),
            "toto",
            "riri",
            Address.create("addresseLine1", "addresseLine2", "Paris", "IDF", "75009", "FR").value(),
            LocalDate.of(1985, 06, 23),
            status,
            null,
            null,
            null,
            null,
            null);

        when(aggregateRootRepository.getByExternalId(any(ClientAggregateExternalId.class)))
            .thenReturn(Result.ok(clientAggregate));
    }
}
