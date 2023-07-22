package exalt.kata.infrastructure.persistence.contracts;

import exalt.kata.domain.core.Action;
import exalt.kata.domain.core.primitives.results.base.Result;

public interface IRepositoryTransactor
{
    Result executeTransaction(Action action) throws Exception;
}
