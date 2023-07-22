package exalt.kata.domain.core.primitives.results;

import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.base.ResultBase;

public class Result<TValue, TPublicError extends PublicError> extends ResultBase
{
    private TValue value;
    private TPublicError publicError;

    public Result(TValue value)
    {
        this.value = value;
    }

    public Result(TPublicError publicError)
    {
        this.publicError = publicError;
    }

    public static <TValue, TPublicError extends PublicError> Result<TValue, TPublicError> ok(TValue value)
    {
        return new Result<>(value);
    }

    public static <TValue, TPublicError extends PublicError> Result<TValue, TPublicError> fail(TPublicError error)
    {
        var result = new Result<TValue, TPublicError>(error);
        result.withError(error.message());
        return result;
    }

    public TValue value()
    {
        return value;
    }

    public TPublicError publicError()
    {
        return publicError;
    }
}
