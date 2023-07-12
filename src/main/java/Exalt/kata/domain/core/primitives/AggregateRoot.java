package exalt.kata.domain.core.primitives;

public abstract class AggregateRoot<TPrimaryKey>
{
    protected TPrimaryKey id;

    protected AggregateRoot(TPrimaryKey id)
    {
        this.id = id;
    }

    public TPrimaryKey getId()
    {
        return id;
    }
}
