package exalt.kata.infrastructure.persistence.contracts.accounting;

import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.infrastructure.primitives.persistence.IQueryAdapter;

import java.time.LocalDateTime;
import java.util.List;

public interface IAccountingQueryAdapter extends IQueryAdapter
{
    List<GetTransaction> getListOfTransaction(ClientAggregateExternalId clientAggregateExternalId, BankAccountAggregateExternalId bankAccountAggregateExternalId, LocalDateTime period);
}
