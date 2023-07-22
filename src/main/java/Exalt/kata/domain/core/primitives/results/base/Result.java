package exalt.kata.domain.core.primitives.results.base;

public class Result<TValue> extends ResultBase
{
    private TValue value;

    public static Result ok()
    {
        return new Result();
    }

    public static Result failed(String errorMessage)
    {
        var result = new Result();
        result.withError(errorMessage);
        return result;
    }

    public static <TValue> Result<TValue> fail(String errorMessage)
    {
        var result = new Result();
        result.withError(errorMessage);
        return result;
    }

    public static <TValue> Result<TValue> ok(TValue value)
    {
        var result = new Result<TValue>();
        result.setValue(value);
        return result;
    }

    public TValue value()
    {
        return value;
    }

    public void setValue(TValue value)
    {
        if (isFailed())
            return;
        this.value = value;
    }
}