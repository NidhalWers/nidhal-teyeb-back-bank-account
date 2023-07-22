package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.regex.Pattern;

public class Email extends SimpleValueObject<String>
{
    private static final String EmailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    private Email(String value)
    {
        super(value);
    }

    public static Result<Email> create(String value)
    {
        var isValid = Pattern.matches(EmailRegex, value);
        if (!isValid) return Result.fail("Invalid email format");

        return Result.ok(new Email(value));
    }

    public static Email createFromDatabase(String email)
    {
        return new Email(email);
    }
}
