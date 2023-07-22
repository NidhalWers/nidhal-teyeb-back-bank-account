package exalt.kata.infrastructure.persistence.implementations;

import exalt.kata.domain.core.Action;
import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.infrastructure.persistence.contracts.IRepositoryTransactor;
import org.springframework.stereotype.Component;

@Component
public class RepositoryTransactor implements IRepositoryTransactor
{

    public Result executeTransaction(Action action) throws Exception
    {
        var transactionResult = action.execute();
        if (transactionResult.isFailed()) throw new Exception(transactionResult.getFirstErrorMessage());

        return transactionResult;
    }
}
