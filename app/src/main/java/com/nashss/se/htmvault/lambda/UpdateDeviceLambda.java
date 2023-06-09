package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.UpdateDeviceRequest;
import com.nashss.se.htmvault.activity.results.UpdateDeviceResult;

public class UpdateDeviceLambda
        extends LambdaActivityRunner<UpdateDeviceRequest, UpdateDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<UpdateDeviceRequest>, LambdaResponse> {

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<UpdateDeviceRequest> input, Context context) {
        return super.runActivity(
                () -> {
                    UpdateDeviceRequest unauthenticatedRequest = input.fromBody(UpdateDeviceRequest.class);
                    return input.fromUserClaims(claims ->
                            UpdateDeviceRequest.builder()
                                    .withControlNumber(unauthenticatedRequest.getControlNumber())
                                    .withSerialNumber(unauthenticatedRequest.getSerialNumber())
                                    .withManufacturer(unauthenticatedRequest.getManufacturer())
                                    .withModel(unauthenticatedRequest.getModel())
                                    .withManufactureDate(unauthenticatedRequest.getManufactureDate())
                                    .withFacilityName(unauthenticatedRequest.getFacilityName())
                                    .withAssignedDepartment(unauthenticatedRequest.getAssignedDepartment())
                                    .withNotes(unauthenticatedRequest.getNotes())
                                    .withCustomerId(claims.get("email"))
                                    .withCustomerName(claims.get("name"))
                                    .build());
                },
                (request, serviceComponent) ->
                        serviceComponent.provideUpdateDeviceActivity().handleRequest(request)
        );
    }
}
