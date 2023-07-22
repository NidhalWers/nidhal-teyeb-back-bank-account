package exalt.kata.common;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregate;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountAggregateId;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IAggregateRootRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BankAccountAggregateRootRepositoryMock
{
    public static void mockSaveAggregateWithSuccess(IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> aggregateRootRepository)
    {
        when(aggregateRootRepository.saveAggregate(
            any(BankAccountAggregate.class)))
            .thenReturn(Result.ok());
    }

    public static void mockGetBankAccountByExternalIdWithNotFound(IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> aggregateRootRepository)
    {
        when(aggregateRootRepository.getByExternalId(
            any(BankAccountAggregateExternalId.class)))
            .thenReturn(Result.fail("bankAccount not found"));
    }

    public static void mockGetBankAccountByExternalIdWithSuccess(
        IAggregateRootRepository<BankAccountAggregate, BankAccountAggregateId, BankAccountAggregateExternalId> aggregateRootRepository,
        ClientAggregateExternalId clientAggregateExternalId,
        BankAccountAggregateExternalId bankAccountAggregateExternalId,
        BankAccountStatus status,
        Currency currency,
        Amount amount,
        Amount overdrawsAuthorized)
    {
        var bankAccountAggregate = BankAccountAggregate.createFromDatabase(
            BankAccountAggregateId.create(),
            bankAccountAggregateExternalId,
            clientAggregateExternalId,
            LocalDateTime.now().minusDays(2),
            status,
            currency,
            amount,
            Tag.createFromDatabase("mockGetByExternalIdWithSuccess"),
            overdrawsAuthorized);

        when(aggregateRootRepository.getByExternalId(any(BankAccountAggregateExternalId.class)))
            .thenReturn(Result.ok(bankAccountAggregate));
    }
}
