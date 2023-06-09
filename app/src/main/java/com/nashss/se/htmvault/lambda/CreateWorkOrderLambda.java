package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateWorkOrderLambda
        extends LambdaActivityRunner<CreateWorkOrderRequest, CreateWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CreateWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request for creating a work order.
     *
     * @param input   The Lambda Function input, an authenticated close work order request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CreateWorkOrderRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
            () -> {
                // the unauthenticated request, after deserializing the json data received
                CreateWorkOrderRequest unauthenticatedRequest = input.fromBody(CreateWorkOrderRequest.class);

                // the final request, including authentication information
                return input.fromUserClaims(claims ->
                    CreateWorkOrderRequest.builder()
                        .withControlNumber(unauthenticatedRequest.getControlNumber())
                        .withWorkOrderType(unauthenticatedRequest.getWorkOrderType())
                        .withProblemReported(unauthenticatedRequest.getProblemReported())
                        .withProblemFound(unauthenticatedRequest.getProblemFound())
                        .withSortOrder(unauthenticatedRequest.getSortOrder())
                        .withCreatedById(claims.get("email"))
                        .withCreatedByName(claims.get("name"))
                        .build());
            },
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideCreateWorkOrderActivity().handleRequest(request)
        );
    }
}
