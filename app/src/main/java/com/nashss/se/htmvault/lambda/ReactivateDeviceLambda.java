package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.ReactivateDeviceRequest;
import com.nashss.se.htmvault.activity.results.ReactivateDeviceResult;

public class ReactivateDeviceLambda
        extends LambdaActivityRunner<ReactivateDeviceRequest, ReactivateDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<ReactivateDeviceRequest>, LambdaResponse> {
    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<ReactivateDeviceRequest> input, Context context) {
        return super.runActivity(
                () -> {
                    ReactivateDeviceRequest unauthenticatedRequest = input.fromPath(path ->
                            ReactivateDeviceRequest.builder()
                                    .withControlNumber(path.get("controlNumber"))
                                    .build());
                    return input.fromUserClaims(claims ->
                            ReactivateDeviceRequest.builder()
                                    .withControlNumber(unauthenticatedRequest.getControlNumber())
                                    .withCustomerId(claims.get("email"))
                                    .withCustomerName(claims.get("name"))
                                    .build());
                },
                (request, serviceComponent) -> serviceComponent.provideReactivateDeviceActivity().handleRequest(request)
        );
    }
}
