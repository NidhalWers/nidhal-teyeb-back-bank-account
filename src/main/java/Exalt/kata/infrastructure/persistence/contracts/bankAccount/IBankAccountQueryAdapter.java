package exalt.kata.infrastructure.persistence.contracts.bankAccount;

import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.primitives.persistence.IQueryAdapter;

import java.util.List;

public interface IBankAccountQueryAdapter extends IQueryAdapter
{
    Result<GetBankAccountByExternalId> getByExternalId(ClientAggregateExternalId clientAggregateExternalId, BankAccountAggregateExternalId bankAccountAggregateExternalId);

    List<BankAccountAggregateExternalId> getNotDeletedBankAccountByClientExternalId(ClientAggregateExternalId id);
}
