package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.results.base.Result;
import exalt.kata.domain.core.primitives.SimpleValueObject;

import java.util.regex.Pattern;

public class PhoneNumber extends SimpleValueObject<String>
{
    private static final String PhoneRegex = "^\\+[1-9]\\d{1,14}$";

    private PhoneNumber(String value)
    {
        super(value);
    }

    public static Result<PhoneNumber> create(String value)
    {
        var isValid = Pattern.matches(PhoneRegex, value);
        if (!isValid) return Result.fail("Invalid phoneNumber format");

        return Result.ok(new PhoneNumber(value));
    }

    public static PhoneNumber createFromDatabase(String number)
    {
        return new PhoneNumber(number);
    }
}
