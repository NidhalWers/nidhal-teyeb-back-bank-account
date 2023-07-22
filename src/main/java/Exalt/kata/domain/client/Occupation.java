package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.SimpleValueObject;

public class Occupation extends SimpleValueObject<String>
{
    private Occupation(String value)
    {
        super(value);
    }

    public static Occupation create(String value)
    {
        return new Occupation(value);
    }

    public static Occupation createFromDatabase(String occupation)
    {
        return new Occupation(occupation);
    }
}
