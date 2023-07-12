package exalt.kata.application.primitives;

import exalt.kata.domain.core.primitives.Result;

public interface IPublicCommandHandler<TPublicCommandRequest extends IPublicCommandRequest, TPublicCommandResponse extends IPublicCommandResponse>
{
    Result<TPublicCommandResponse> handleCommand(TPublicCommandRequest request);
}
