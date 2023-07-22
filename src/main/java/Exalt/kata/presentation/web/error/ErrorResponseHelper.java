package exalt.kata.presentation.web.error;

import exalt.kata.domain.core.primitives.ErrorCode;
import org.springframework.http.HttpStatus;

public class ErrorResponseHelper
{
    public static HttpStatus translate(ErrorCode errorCode){
        switch (errorCode){
            case param_error:
            case invalid_amount:
            case invalid_currency:
            case invalid_country_code:
                return HttpStatus.BAD_REQUEST;

            case resource_not_found:
                return HttpStatus.NOT_FOUND;

            case bank_account_error:
            case client_error:
            case transaction_error:
                return HttpStatus.FORBIDDEN;

            case client_is_not_bank_account_owner:
                return HttpStatus.UNAUTHORIZED;

            case internal:
                return HttpStatus.INTERNAL_SERVER_ERROR;

            default:
                return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
}
