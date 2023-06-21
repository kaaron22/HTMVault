package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetDeviceWorkOrdersLambda
        extends LambdaActivityRunner<GetDeviceWorkOrdersRequest, GetDeviceWorkOrdersResult>
        implements RequestHandler<LambdaRequest<GetDeviceWorkOrdersRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request for getting all the work orders for a specified device.
     *
     * @param input   The Lambda Function input, a request to get a device's work orders
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<GetDeviceWorkOrdersRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            // the request built using the path (device id) and query parameters (sort order)
            () -> input.fromPathAndQuery((path, query) ->
                GetDeviceWorkOrdersRequest.builder()
                    .withControlNumber(path.get("controlNumber"))
                    .withSortOrder(query.get("order"))
                    .build()),
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideGetDeviceWorkOrdersActivity().handleRequest(request)
        );
    }
}
