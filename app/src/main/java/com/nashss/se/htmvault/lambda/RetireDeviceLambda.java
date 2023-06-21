package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetireDeviceLambda
        extends LambdaActivityRunner<RetireDeviceRequest, RetireDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<RetireDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to retire a device no longer in use (i.e. a rental returned,
     * or a device that has reached "end-of-life" and is no longer supported by the manufacturer)
     *
     * @param input   The Lambda Function input, an authenticated request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<RetireDeviceRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
            () -> {
                // the unauthenticated request, built from the path parameter (device id)
                RetireDeviceRequest unauthenticatedRequest = input.fromPath(path ->
                    RetireDeviceRequest.builder()
                        .withControlNumber(path.get("controlNumber"))
                        .build());

                // the final request, including authentication information
                return input.fromUserClaims(claims ->
                    RetireDeviceRequest.builder()
                        .withControlNumber(unauthenticatedRequest.getControlNumber())
                        .withCustomerId(claims.get("email"))
                        .withCustomerName(claims.get("name"))
                        .build());
            },
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideRetireDeviceActivity().handleRequest(request)
        );
    }
}
