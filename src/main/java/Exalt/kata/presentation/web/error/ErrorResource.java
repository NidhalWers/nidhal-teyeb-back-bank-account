package exalt.kata.presentation.web.error;

import exalt.kata.domain.core.primitives.ErrorCode;

public record ErrorResource(
    ErrorCode code,
    String message)
{
}
