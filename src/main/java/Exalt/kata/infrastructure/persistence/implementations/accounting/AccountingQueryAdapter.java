package exalt.kata.infrastructure.persistence.implementations.accounting;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.accounting.TransactionType;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.infrastructure.persistence.contracts.accounting.GetTransaction;
import exalt.kata.infrastructure.persistence.contracts.accounting.IAccountingQueryAdapter;
import exalt.kata.infrastructure.persistence.implementations.database.accounting.IAccountingTransactionTableRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Repository
public class AccountingQueryAdapter implements IAccountingQueryAdapter
{
    private IAccountingTransactionTableRepository dbContext;

    public AccountingQueryAdapter(IAccountingTransactionTableRepository context)
    {
        dbContext = context;
    }

    public List<GetTransaction> getListOfTransaction(ClientAggregateExternalId clientAggregateExternalId, BankAccountAggregateExternalId bankAccountAggregateExternalId, LocalDateTime period)
    {
        return dbContext.findAll().stream()
            .filter(x -> x.getClientExternalId().equals(clientAggregateExternalId.value())
                && x.getBankAccountExternalId().equals(bankAccountAggregateExternalId.value())
                && x.getExecutionDate().isAfter(period) )
            .map(x -> new GetTransaction(
                BankAccountAggregateExternalId.createFromDatabase(x.getBankAccountExternalId()),
                TransactionType.valueOf(x.getType()),
                Amount.createFromDatabase(x.getAmount()),
                Currency.valueOf(x.getCurrency()),
                x.getExecutionDate(),
                Tag.createFromDatabase(x.getTag())))
            .sorted(Comparator.comparing(GetTransaction::executionDate, Comparator.reverseOrder()))
            .toList();
    }
}
