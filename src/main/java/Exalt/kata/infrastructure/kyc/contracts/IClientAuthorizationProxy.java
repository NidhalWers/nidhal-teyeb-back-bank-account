package exalt.kata.infrastructure.kyc.contracts;

import exalt.kata.domain.CountryCode;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.IdentificationNumber;
import exalt.kata.domain.core.primitives.results.VoidResult;

public interface IClientAuthorizationProxy
{
    VoidResult isClientAuthorized(ClientAggregateExternalId clientAggregateExternalId, CountryCode nationality, IdentificationNumber identificationNumber);
}
