package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;

public class SearchDevicesLambda
        extends LambdaActivityRunner<SearchDevicesRequest, SearchDevicesResult>
        implements RequestHandler<LambdaRequest<SearchDevicesRequest>, LambdaResponse> {
    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<SearchDevicesRequest> input, Context context) {
        return super.runActivity(
                () -> input.fromQuery(query ->
                        SearchDevicesRequest.builder()
                                .withCriteria(query.get("q"))
                                .build()),
                (request, serviceComponent) ->
                        serviceComponent.provideSearchDevicesActivity().handleRequest(request)
        );
    }
}
