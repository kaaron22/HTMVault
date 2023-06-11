package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;

public class CreateWorkOrderLambda
        extends LambdaActivityRunner<CreateWorkOrderRequest, CreateWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CreateWorkOrderRequest>, LambdaResponse> {
    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CreateWorkOrderRequest> input, Context context) {
        return null;
    }
}
