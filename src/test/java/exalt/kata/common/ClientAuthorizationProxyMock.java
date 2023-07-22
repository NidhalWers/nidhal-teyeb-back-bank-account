package exalt.kata.common;

import exalt.kata.domain.CountryCode;
import exalt.kata.domain.client.ClientAggregateExternalId;
import exalt.kata.domain.client.IdentificationNumber;
import exalt.kata.domain.core.primitives.results.VoidResult;
import exalt.kata.infrastructure.kyc.contracts.IClientAuthorizationProxy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ClientAuthorizationProxyMock
{
    public static void mockIsClientAuthorizedFailed(IClientAuthorizationProxy clientAuthorizationProxy)
    {
        when(clientAuthorizationProxy.isClientAuthorized(
            any(ClientAggregateExternalId.class),
            any(CountryCode.class),
            any(IdentificationNumber.class)))
            .thenReturn(VoidResult.fail("the client cannot be authorized"));
    }

    public static void mockIsClientAuthorizedSuccess(IClientAuthorizationProxy clientAuthorizationProxy)
    {
        when(clientAuthorizationProxy.isClientAuthorized(
            any(ClientAggregateExternalId.class),
            any(CountryCode.class),
            any(IdentificationNumber.class)))
            .thenReturn(VoidResult.ok());
    }
}
