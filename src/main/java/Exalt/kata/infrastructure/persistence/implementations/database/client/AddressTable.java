package exalt.kata.infrastructure.persistence.implementations.database.client;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class AddressTable
{
    @Id
    @GeneratedValue
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String postalCode;
    private String countryCode;

    public AddressTable()
    {
    }

    public Long getId()
    {
        return id;
    }

    public String getAddressLine1()
    {
        return addressLine1;
    }

    public AddressTable setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
        return this;
    }

    public String getAddressLine2()
    {
        return addressLine2;
    }

    public AddressTable setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
        return this;
    }

    public String getCity()
    {
        return city;
    }

    public AddressTable setCity(String city)
    {
        this.city = city;
        return this;
    }

    public String getRegion()
    {
        return region;
    }

    public AddressTable setRegion(String region)
    {
        this.region = region;
        return this;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public AddressTable setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
        return this;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public AddressTable setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
        return this;
    }
}
