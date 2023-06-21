package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.ReactivateDeviceRequest;
import com.nashss.se.htmvault.activity.results.ReactivateDeviceResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReactivateDeviceLambda
        extends LambdaActivityRunner<ReactivateDeviceRequest, ReactivateDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<ReactivateDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to reactivate/un-retire a device.
     *
     * @param input   The Lambda Function input, an authenticated request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<ReactivateDeviceRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            () -> {
                // the unauthenticated request, after deserializing the json data received
                ReactivateDeviceRequest unauthenticatedRequest = input.fromBody(ReactivateDeviceRequest.class);

                // the final request, including authentication information
                return input.fromUserClaims(claims ->
                    ReactivateDeviceRequest.builder()
                        .withControlNumber(unauthenticatedRequest.getControlNumber())
                        .withCustomerId(claims.get("email"))
                        .withCustomerName(claims.get("name"))
                        .build());
            },
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideReactivateDeviceActivity().handleRequest(request)
        );
    }
}
