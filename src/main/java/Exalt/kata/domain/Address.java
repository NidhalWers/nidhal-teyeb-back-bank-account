package exalt.kata.domain;

import exalt.kata.domain.core.primitives.results.base.Result;

public class Address
{
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String postalCode;
    private CountryCode countryCode;

    private Address(
        String addressLine1,
        String addressLine2,
        String city,
        String region,
        String postalCode,
        CountryCode countryCode)
    {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.region = region;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
    }

    public static Result<Address> create(
        String addressLine1,
        String addressLine2,
        String city,
        String region,
        String postalCode,
        String countryCodeInput)
    {
        if (addressLine1.isBlank()) return Result.fail("addressLine1 should not be blank");
        if (city.isBlank()) return Result.fail("city should not be blank");
        if (region.isBlank()) return Result.fail("region should not be blank");
        if (postalCode.isBlank()) return Result.fail("postalCode should not be blank");
        CountryCode countryCode;
        try
        {
            countryCode = CountryCode.valueOf(countryCodeInput);
        } catch (IllegalArgumentException e)
        {
            return Result.fail(String.format("%s is not a valid CountryCode", countryCodeInput));
        }

        return Result.ok(new Address(
            addressLine1,
            addressLine2,
            city,
            region,
            postalCode,
            countryCode));
    }


    public static Address createFromDatabase(
        String addressLine1,
        String addressLine2,
        String city,
        String region,
        String postalCode,
        CountryCode countryCode)
    {
        return new Address(
            addressLine1,
            addressLine2,
            city,
            region,
            postalCode,
            countryCode);
    }

    public String getAddressLine1()
    {
        return addressLine1;
    }

    public String getAddressLine2()
    {
        return addressLine2;
    }

    public String getCity()
    {
        return city;
    }

    public String getRegion()
    {
        return region;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public CountryCode getCountryCode()
    {
        return countryCode;
    }
}
