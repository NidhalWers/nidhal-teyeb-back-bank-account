package exalt.kata.application.contracts;

public record AddressDTO(
    String addressLine1,
    String addressLine2,
    String city,
    String region,
    String postalCode,
    String countryCode)
{
}
