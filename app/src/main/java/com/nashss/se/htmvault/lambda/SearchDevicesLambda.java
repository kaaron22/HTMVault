package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchDevicesLambda
        extends LambdaActivityRunner<SearchDevicesRequest, SearchDevicesResult>
        implements RequestHandler<LambdaRequest<SearchDevicesRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to scan for a list of devices matching search criteria.
     *
     * @param input   The Lambda Function input, a SearchDevicesRequest
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<SearchDevicesRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            // the request, built using query parameters
            () -> input.fromQuery(query ->
                SearchDevicesRequest.builder()
                    .withCriteria(query.get("q"))
                    .build()),
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideSearchDevicesActivity().handleRequest(request)
        );
    }
}
