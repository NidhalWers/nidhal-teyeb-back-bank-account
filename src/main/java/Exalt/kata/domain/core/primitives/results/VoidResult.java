package exalt.kata.domain.core.primitives.results;

import exalt.kata.domain.core.primitives.results.base.ResultBase;

public class VoidResult extends ResultBase
{
    public static VoidResult ok()
    {
        return new VoidResult();
    }

    public static VoidResult fail(String errorMessage)
    {
        var result = new VoidResult();
        result.withError(errorMessage);
        return result;
    }
}
