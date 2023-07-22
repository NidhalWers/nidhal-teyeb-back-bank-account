package exalt.kata.domain.client;

import exalt.kata.domain.core.primitives.SimpleValueObject;

public class IdentificationNumber extends SimpleValueObject<String>
{
    private IdentificationNumber(String value)
    {
        super(value);
    }

    public static IdentificationNumber create(String identificationNumber)
    {
        return new IdentificationNumber(identificationNumber);
    }

    public static IdentificationNumber createFromDatabase(String identificationNumber)
    {
        return new IdentificationNumber(identificationNumber);
    }
}
