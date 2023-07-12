package exalt.kata.infrastructure.primitives.persistence;

import exalt.kata.domain.core.Action;
import exalt.kata.domain.core.primitives.Result;

public interface IUnitOfWork
{
    Result executeTransaction(Action action);
}
