package exalt.kata.presentation.web.error;

import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;

public class ApplicationException extends RuntimeException
{
    private ErrorCode errorCode;

    public ApplicationException(String message, ErrorCode errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(PublicError error)
    {
        this(error.message(), error.errorCode());
    }

    public ErrorCode getErrorCode()
    {
        return errorCode;
    }
}
