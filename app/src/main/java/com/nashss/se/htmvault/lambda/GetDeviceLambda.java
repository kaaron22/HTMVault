package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.GetDeviceRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceResult;

public class GetDeviceLambda
        extends LambdaActivityRunner<GetDeviceRequest, GetDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<GetDeviceRequest>, LambdaResponse> {

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<GetDeviceRequest> input, Context context) {
        return super.runActivity(
                () -> {
                    GetDeviceRequest unauthenticatedRequest = input.fromQuery(GetDeviceRequest.class);
                    return input.fromUserClaims(claims ->
                            GetDeviceRequest.builder()
                                    .withControlNumber(unauthenticatedRequest.getControlNumber())
                                    .withCustomerId(claims.get("email"))
                                    .withCustomerName(claims.get("name"))
                                    .build());
                },
                (request, serviceComponent) ->
                        serviceComponent.provideGetDeviceActivity().handleRequest(request)
        );
    }
}
