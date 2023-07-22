package exalt.kata.common;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.accounting.TransactionType;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.infrastructure.persistence.contracts.accounting.GetTransaction;
import exalt.kata.infrastructure.persistence.contracts.accounting.IAccountingQueryAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AccountingQueryAdapterMock
{
    public static void mockQueryGetListOfTransaction(IAccountingQueryAdapter queryAdapter, int numberOfTransactions)
    {
        var transaction = new GetTransaction(
            BankAccountAggregateExternalId.createFromDatabase(UUID.randomUUID()),
            TransactionType.CREDIT,
            Amount.createForBalance(25L),
            Currency.EUR,
            LocalDateTime.now(),
            Tag.createFromDatabase("mockQueryGetListOfTransaction"));

        List<GetTransaction> result = new ArrayList<>();
        for(int i = 0; i < numberOfTransactions; i++) result.add(transaction);

        when(queryAdapter.getListOfTransaction(
            any(ClientAggregateExternalId.class),
            any(BankAccountAggregateExternalId.class),
            any(LocalDateTime.class)))
            .thenReturn(result);
    }
}
