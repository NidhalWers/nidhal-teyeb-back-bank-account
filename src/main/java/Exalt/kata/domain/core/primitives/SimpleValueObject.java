package exalt.kata.domain.core.primitives;

import java.util.Objects;

public abstract class SimpleValueObject<T>
{
    private final T value;

    protected SimpleValueObject(T value)
    {
        this.value = value;
    }

    public T value()
    {
        return value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleValueObject<?> that = (SimpleValueObject<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value);
    }
}
