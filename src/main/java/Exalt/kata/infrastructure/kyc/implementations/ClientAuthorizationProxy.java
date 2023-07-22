package exalt.kata.infrastructure.kyc.implementations;

import exalt.kata.domain.CountryCode;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.IdentificationNumber;
import exalt.kata.domain.core.primitives.results.VoidResult;
import exalt.kata.infrastructure.kyc.contracts.IClientAuthorizationProxy;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthorizationProxy implements IClientAuthorizationProxy
{
    /**
     * the implementation does not really matter right now
     */
    @Override
    public VoidResult isClientAuthorized(ClientAggregateExternalId clientAggregateExternalId, CountryCode nationality, IdentificationNumber identificationNumber)
    {
        if (false) return VoidResult.fail("The client is a declared criminal in Europe");
        return VoidResult.ok();
    }
}
