package exalt.kata.domain;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

public class Tag extends SimpleValueObject<String>
{
    private Tag(String value)
    {
        super(value);
    }

    public static Result<Tag> create(String tag)
    {
        if (tag.length() > 255) return Result.fail("The field Tag must match the regular expression '^.{0,255}$'.");
        return Result.ok(new Tag(tag));
    }

    public static Tag createFromDatabase(String tag)
    {
        return new Tag(tag);
    }
}
