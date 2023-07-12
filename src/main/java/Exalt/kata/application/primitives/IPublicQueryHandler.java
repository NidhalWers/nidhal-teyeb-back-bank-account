package exalt.kata.application.primitives;

import exalt.kata.domain.core.primitives.Result;

public interface IPublicQueryHandler<TPublicQueryRequest extends IPublicQueryRequest, TPublicQueryResponse extends IPublicQueryResponse>
{
    Result<TPublicQueryResponse> handleQuery(TPublicQueryRequest request);
}
