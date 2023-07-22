package exalt.kata.common;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.GetBankAccountByExternalId;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BankAccountQueryAdapterMock
{
    public static void mockQueryGetBankAccountByExternalIdWithSucceess(
        IBankAccountQueryAdapter queryAdapter,
        BankAccountAggregateExternalId bankAccountAggregateExternalId,
        long amount)
    {
        var result = new GetBankAccountByExternalId(
            bankAccountAggregateExternalId,
            Amount.createForBalance(amount),
            Currency.EUR,
            BankAccountStatus.ACTIVE,
            Tag.createFromDatabase("mockQueryGetClientByExternalIdWithNotFound"));

        when(queryAdapter.getByExternalId(any(ClientAggregateExternalId.class), any(BankAccountAggregateExternalId.class)))
            .thenReturn(Result.ok(result));
    }

    public static void mockQueryGetBankAccountByExternalIdWithNotFound(IBankAccountQueryAdapter queryAdapter)
    {
        when(queryAdapter.getByExternalId(any(ClientAggregateExternalId.class), any(BankAccountAggregateExternalId.class)))
            .thenReturn(Result.fail("Bank account not found"));
    }

    public static void mockGetNotDeletedBankAccountByClientExternalId(
        IBankAccountQueryAdapter queryAdapter,
        int numberOfBankAccounts)
    {
        when(queryAdapter.getNotDeletedBankAccountByClientExternalId(any(ClientAggregateExternalId.class)))
            .thenReturn(Stream.generate(() -> BankAccountAggregateExternalId.createFromDatabase(UUID.randomUUID()))
                .limit(numberOfBankAccounts)
                .collect(Collectors.toList()));
    }
}
