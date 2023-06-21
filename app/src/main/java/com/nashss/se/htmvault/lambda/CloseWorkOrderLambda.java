package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloseWorkOrderLambda
        extends LambdaActivityRunner<CloseWorkOrderRequest, CloseWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CloseWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request for closing a work order.
     *
     * @param input   The Lambda Function input, an authenticated close work order request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CloseWorkOrderRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            () -> {
                // the unauthenticated request, using the work order id path parameter
                CloseWorkOrderRequest unauthenticatedRequest = input.fromPath(path ->
                    CloseWorkOrderRequest.builder()
                        .withWorkOrderId(path.get("workOrderId"))
                        .build());
                // the final request, including authentication information
                return input.fromUserClaims(claims ->
                    CloseWorkOrderRequest.builder()
                        .withWorkOrderId(unauthenticatedRequest.getWorkOrderId())
                        .withCustomerId(claims.get("email"))
                        .withCustomerName(claims.get("name"))
                        .build());
            },
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideCloseWorkOrderActivity().handleRequest(request)
        );
    }
}
