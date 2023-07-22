package exalt.kata.application.primitives;

import exalt.kata.domain.core.primitives.PublicError;
import exalt.kata.domain.core.primitives.results.Result;

public interface IPublicQueryHandler<
    TPublicQueryRequest extends IPublicQueryRequest,
    TPublicQueryResponse extends IPublicQueryResponse,
    TPublicApplicationError extends PublicError>
{
    Result<TPublicQueryResponse, TPublicApplicationError> handleQuery(TPublicQueryRequest request);
}
