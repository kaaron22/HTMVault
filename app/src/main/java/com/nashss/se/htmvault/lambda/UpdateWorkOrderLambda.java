package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.UpdateWorkOrderResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateWorkOrderLambda
        extends LambdaActivityRunner<UpdateWorkOrderRequest, UpdateWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<UpdateWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<UpdateWorkOrderRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
                () -> {
                    UpdateWorkOrderRequest unauthenticatedRequest = input.fromBody(UpdateWorkOrderRequest.class);
                    return input.fromUserClaims(claims ->
                            UpdateWorkOrderRequest.builder()
                                    .withWorkOrderId(unauthenticatedRequest.getWorkOrderId())
                                    .withWorkOrderType(unauthenticatedRequest.getWorkOrderType())
                                    .withWorkOrderAwaitStatus(unauthenticatedRequest.getWorkOrderAwaitStatus())
                                    .withProblemReported(unauthenticatedRequest.getProblemReported())
                                    .withProblemFound(unauthenticatedRequest.getProblemFound())
                                    .withSummary(unauthenticatedRequest.getSummary())
                                    .withCompletionDateTime(unauthenticatedRequest.getCompletionDateTime())
                                    .withCustomerId(claims.get("email"))
                                    .withCustomerName(claims.get("name"))
                                    .build(),
                    (request, serviceComponent) -> serviceComponent
                }
        )

    }
}
