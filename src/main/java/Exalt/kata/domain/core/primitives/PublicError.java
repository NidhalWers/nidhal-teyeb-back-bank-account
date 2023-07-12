package exalt.kata.domain.core.primitives;

public record PublicError(
    String message,
    ErrorCode errorCode)
{
}
