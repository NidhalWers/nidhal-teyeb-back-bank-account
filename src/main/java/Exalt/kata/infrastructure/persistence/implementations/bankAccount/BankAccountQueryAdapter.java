package exalt.kata.infrastructure.persistence.implementations.bankAccount;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountStatus;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.GetBankAccountByExternalId;
import exalt.kata.infrastructure.persistence.contracts.bankAccount.IBankAccountQueryAdapter;
import exalt.kata.infrastructure.persistence.implementations.database.bankAccount.IBankAccountTableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BankAccountQueryAdapter implements IBankAccountQueryAdapter
{
    private IBankAccountTableRepository dbContext;

    public BankAccountQueryAdapter(IBankAccountTableRepository context)
    {
        dbContext = context;
    }

    public Result<GetBankAccountByExternalId> getByExternalId(ClientAggregateExternalId clientAggregateExternalId, BankAccountAggregateExternalId bankAccountAggregateExternalId)
    {
        var externalObjectResult = dbContext.findAll().stream()
            .filter(x -> x.getClientExternalId().equals(clientAggregateExternalId.value()) && x.getExternalId().equals(bankAccountAggregateExternalId.value()))
            .map(x -> new GetBankAccountByExternalId(
                BankAccountAggregateExternalId.createFromDatabase(x.getExternalId()),
                Amount.createFromDatabase(x.getAmount()),
                Currency.valueOf(x.getCurrency()),
                BankAccountStatus.valueOf(x.getStatus()),
                Tag.createFromDatabase(x.getTag())))
            .findFirst();
        if(! externalObjectResult.isPresent()) return Result.fail(String.format("bankAccount %s not found for client %s", bankAccountAggregateExternalId.value(), clientAggregateExternalId.value()));

        return Result.ok(externalObjectResult.get());
    }

    public List<BankAccountAggregateExternalId> getNotDeletedBankAccountByClientExternalId(ClientAggregateExternalId clientAggregateExternalId)
    {
        return dbContext.findAll().stream()
            .filter(x -> x.getClientExternalId().equals(clientAggregateExternalId.value())
                && !x.getStatus().equals(BankAccountStatus.DELETED.name()))
            .map(x -> BankAccountAggregateExternalId.createFromDatabase(x.getClientExternalId()))
            .toList();
    }
}
