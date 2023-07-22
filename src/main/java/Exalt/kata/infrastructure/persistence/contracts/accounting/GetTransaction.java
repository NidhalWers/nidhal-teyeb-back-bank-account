package exalt.kata.infrastructure.persistence.contracts.accounting;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.accounting.TransactionType;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;

import java.time.LocalDateTime;

public record GetTransaction(
    BankAccountAggregateExternalId bankAccountAggregateExternalId,
    TransactionType type,
    Amount amount,
    Currency currency,
    LocalDateTime executionDate,
    Tag tag)
{
}
