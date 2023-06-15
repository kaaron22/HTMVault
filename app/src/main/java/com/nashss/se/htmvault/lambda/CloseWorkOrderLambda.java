package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloseWorkOrderLambda
        extends LambdaActivityRunner<CloseWorkOrderRequest, CloseWorkOrderResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CloseWorkOrderRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CloseWorkOrderRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(

        );
    }
}
