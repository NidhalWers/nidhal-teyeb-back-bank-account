package exalt.kata.domain.bankAccount;

import exalt.kata.domain.Amount;
import exalt.kata.domain.Currency;
import exalt.kata.domain.Tag;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.core.primitives.AggregateRoot;
import exalt.kata.domain.core.primitives.results.base.Result;

import java.time.LocalDateTime;

public class BankAccountAggregate extends AggregateRoot<BankAccountAggregateId>
{
    private BankAccountAggregateExternalId externalId;
    private ClientAggregateExternalId clientAggregateExternalId;
    private LocalDateTime creationDate;
    private BankAccountStatus status;
    private Amount amount;
    private Currency currency;
    private Tag tag;
    private Amount overdrawnAuthorized;

    private BankAccountAggregate(
        BankAccountAggregateId id,
        BankAccountAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        LocalDateTime creationDate,
        BankAccountStatus status,
        Amount amount,
        Currency currency,
        Tag tag,
        Amount overdrawnAuthorized)
    {
        super(id);
        this.externalId = externalId;
        this.clientAggregateExternalId = clientAggregateExternalId;
        this.creationDate = creationDate;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.tag = tag;
        this.overdrawnAuthorized = overdrawnAuthorized;
    }

    public static Result<BankAccountAggregate> create(
        BankAccountAggregateId id,
        BankAccountAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        Currency currency,
        Tag tag)
    {
        return Result.ok(new BankAccountAggregate(
            id,
            externalId,
            clientAggregateExternalId,
            LocalDateTime.now(),
            BankAccountStatus.ACTIVE,
            Amount.NullAmount,
            currency,
            tag,
            Amount.NullAmount));
    }

    public static BankAccountAggregate createFromDatabase(
        BankAccountAggregateId id,
        BankAccountAggregateExternalId externalId,
        ClientAggregateExternalId clientAggregateExternalId,
        LocalDateTime creationDate,
        BankAccountStatus bankAccountStatus,
        Currency currency,
        Amount amount,
        Tag tag,
        Amount overdrawnAuthorized)
    {
        return new BankAccountAggregate(
            id,
            externalId,
            clientAggregateExternalId,
            creationDate,
            bankAccountStatus,
            amount,
            currency,
            tag, overdrawnAuthorized);
    }

    public BankAccountAggregateExternalId getExternalId()
    {
        return externalId;
    }

    public ClientAggregateExternalId getClientExternalId()
    {
        return clientAggregateExternalId;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    public BankAccountStatus getStatus()
    {
        return status;
    }

    public Amount getAmount()
    {
        return amount;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public Tag getTag()
    {
        return tag;
    }

    public Amount getOverdrawnAuthorized()
    {
        return overdrawnAuthorized;
    }

    public Result authorizeOverdrawn(Amount overdrawnAuthorized){
        if (status == BankAccountStatus.BLOCKED || status == BankAccountStatus.DELETED)
            return Result.failed(String.format("To authorize overdrawn, the BankAccount status cannot be %s", BankAccountStatus.BLOCKED));

        this.overdrawnAuthorized = overdrawnAuthorized;

        return Result.ok();
    }

    public Result debitMoney(Amount amountDebited)
    {
        if (status == BankAccountStatus.UNAUTHORIZED_TO_DEBIT || status == BankAccountStatus.BLOCKED || status == BankAccountStatus.DELETED)
            return Result.failed(String.format("To DebitMoney, the BankAccount status must be %s and not %s", BankAccountStatus.ACTIVE, status));

        var newAmount = Amount.createForBalance(amount.value() - amountDebited.value());

        if (newAmount.lesserThan(Amount.NullAmount.minus(overdrawnAuthorized)))
            return Result.fail(String.format( "the account is not authorized for an overdraft of %d", newAmount.value()));
        if (newAmount.lesserThanZero())
            overdraws();

        amount = newAmount;
        return Result.ok();
    }

    public Result creditMoney(Amount amountCredited)
    {
        if (status == BankAccountStatus.UNAUTHORIZED_TO_CREDIT || status == BankAccountStatus.BLOCKED || status == BankAccountStatus.DELETED)
            return Result.failed(String.format("To CreditMoney, the BankAccount status must be %s and not %s", BankAccountStatus.ACTIVE, status));

        var newAmount = Amount.createForBalance(amount.value() + amountCredited.value());
        amount = newAmount;

        if(amount.greaterThanZero()) status = BankAccountStatus.ACTIVE;

        return Result.ok();
    }
    private Result overdraws(){
        if (status == BankAccountStatus.BLOCKED  || status == BankAccountStatus.DELETED)
            return Result.failed(String.format("To overdraws, the BankAccount status cannot be %s", BankAccountStatus.BLOCKED));

        status = BankAccountStatus.UNAUTHORIZED_TO_DEBIT;
        return Result.ok();
    }

    public Result delete()
    {
        if (status == BankAccountStatus.DELETED)
            return Result.fail("Bank account already deleted");
        if (amount.differentFromZero())
            return Result.fail(String.format("Bank account can not be deleted with a balance of %d", amount.value()));

        this.status = BankAccountStatus.DELETED;
        return Result.ok();
    }
}
