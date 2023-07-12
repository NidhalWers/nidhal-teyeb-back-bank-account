package exalt.kata.domain.core.primitives;

public enum ErrorCode
{
    param_error,
    client_error,
    bank_account_error,
    resource_not_found,
    client_is_not_bank_account_owner,
    invalid_amount,
    invalid_currency,
    transaction_error,
    internal,
    other, invalid_country_code,
}
