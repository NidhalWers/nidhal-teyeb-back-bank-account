package exalt.kata.infrastructure.persistence.contracts.bankAccount;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.bankAccount.BankAccountAggregateExternalId;
import exalt.kata.domain.bankAccount.BankAccountStatus;

public record GetBankAccountByExternalId(
    BankAccountAggregateExternalId externalId,
    Amount amount,
    Currency currency,
    BankAccountStatus status,
    Tag tag)
{
}
