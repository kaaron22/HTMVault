package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateWorkOrderLambda
        extends LambdaActivityRunner<CreateWorkOrderRequest, CreateWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CreateWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CreateWorkOrderRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
                () -> {
                    CreateWorkOrderRequest unauthenticatedRequest = input.fromBody(CreateWorkOrderRequest.class);
                    return input.fromUserClaims(claims ->
                            CreateWorkOrderRequest.builder()
                                    .withControlNumber(unauthenticatedRequest.getControlNumber())
                                    .withWorkOrderType(unauthenticatedRequest.getWorkOrderType())
                                    .withProblemReported(unauthenticatedRequest.getProblemReported())
                                    .withProblemFound(unauthenticatedRequest.getProblemFound())
                                    .withCreatedById(claims.get("email"))
                                    .withCreatedByName(claims.get("name"))
                                    .build());
                },
                (request, serviceComponent) ->
                        serviceComponent.provideCreateWorkOrderActivity().handleRequest(request)
        );
    }
}
