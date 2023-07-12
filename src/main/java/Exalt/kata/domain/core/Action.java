package exalt.kata.domain.core;

import exalt.kata.domain.core.primitives.Result;

@FunctionalInterface
public interface Action
{
    Result execute();
}
