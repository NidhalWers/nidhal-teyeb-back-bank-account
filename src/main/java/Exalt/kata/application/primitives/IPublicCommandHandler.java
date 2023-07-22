package exalt.kata.application.primitives;

import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;

public interface IPublicCommandHandler<
    TPublicCommandRequest extends IPublicCommandRequest,
    TPublicCommandResponse extends IPublicCommandResponse,
    TPublicApplicationError extends PublicError>
{
    Result<TPublicCommandResponse, TPublicApplicationError> handleCommand(TPublicCommandRequest request);
}
