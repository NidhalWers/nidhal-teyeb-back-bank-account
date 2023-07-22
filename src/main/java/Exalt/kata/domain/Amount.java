package exalt.kata.domain;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

public class Amount extends SimpleValueObject<Long>
{
    private Amount(Long value)
    {
        super(value);
    }

    public static final Amount NullAmount = new Amount(0L);
    public static Result<Amount> createForTransaction(Long amount)
    {
        if (amount < 0) return Result.fail("Transaction amount cannot be null or negative");
        return Result.ok(new Amount(amount));
    }

    public static Amount createForBalance(Long amount)
    {
        return new Amount(amount);
    }

    public static Result<Amount> createForOverdrawn(Long amount)
    {
        if (amount < 0) return Result.fail("Overdrawn amount cannot be null or negative");
        return Result.ok(new Amount(amount));
    }

    public static Amount createFromDatabase(Long amount)
    {
        return new Amount(amount);
    }

    public Amount minus(Amount otherAmount)
    {
        if (otherAmount == null) return this;
        return new Amount(this.value() - otherAmount.value());
    }
    public boolean lesserThan(Amount otherAmount)
    {
        return this.value() < otherAmount.value();
    }
    public boolean lesserThanZero()
    {
        return this.value() < 0;
    }

    public boolean greaterThanZero()
    {
        return this.value() > 0;
    }

    public boolean differentFromZero()
    {
        return this.value() != 0;
    }
}
