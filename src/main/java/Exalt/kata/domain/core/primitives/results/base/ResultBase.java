package exalt.kata.domain.core.primitives.results.base;

import java.util.ArrayList;
import java.util.List;

public abstract class ResultBase
{
    private List<String> errors;

    public ResultBase()
    {
        this.errors = new ArrayList<>();
    }

    public boolean isFailed()
    {
        return !errors.isEmpty();
    }

    public boolean isSuccess()
    {
        return !isFailed();
    }

    public ResultBase withErrors(List<String> errors)
    {
        this.errors = errors;
        return this;
    }

    public ResultBase withError(String error)
    {
        this.errors.add(error);
        return this;
    }

    public String getFirstErrorMessage()
    {
        return errors.get(0);
    }
}
