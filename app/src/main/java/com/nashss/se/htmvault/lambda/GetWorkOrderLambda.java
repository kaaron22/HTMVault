package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.GetWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.GetWorkOrderResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetWorkOrderLambda
        extends LambdaActivityRunner<GetWorkOrderRequest, GetWorkOrderResult>
        implements RequestHandler<LambdaRequest<GetWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to get a work order.
     *
     * @param input   The Lambda Function input, a request for obtaining a work order
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<GetWorkOrderRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            // the request, built using the path parameter (work order id)
            () -> input.fromPath(path ->
                GetWorkOrderRequest.builder()
                    .withWorkOrderId(path.get("workOrderId"))
                    .build()),
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideGetWorkOrderActivity().handleRequest(request)
        );
    }
}
