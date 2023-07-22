package exalt.kata.infrastructure.persistence.implementations;

import exalt.kata.domain.core.Action;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.persistence.contracts.IRepositoryTransactor;
import exalt.kata.infrastructure.primitives.persistence.IUnitOfWork;
import org.springframework.stereotype.Component;

@Component
public class UnitOfWork implements IUnitOfWork
{
    private final IRepositoryTransactor repositoryTransactor;

    public UnitOfWork(IRepositoryTransactor transactor)
    {
        repositoryTransactor = transactor;
    }

    public Result executeTransaction(Action action)
    {
        try
        {
            return repositoryTransactor.executeTransaction(action);
        } catch (Exception e)
        {
            return Result.fail(e.getMessage());
        }
    }
}
