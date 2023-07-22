package exalt.kata.domain.core;

import exalt.kata.domain.core.primitives.results.base.Result;

@FunctionalInterface
public interface Action
{
    Result execute();
}
