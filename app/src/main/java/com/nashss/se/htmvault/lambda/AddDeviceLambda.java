package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;

public class AddDeviceLambda
        extends LambdaActivityRunner<AddDeviceRequest, AddDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<AddDeviceRequest>, LambdaResponse> {

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<AddDeviceRequest> input, Context context) {
        return super.runActivity(
                () -> {
                    AddDeviceRequest unauthenticatedRequest = input.fromBody(AddDeviceRequest.class);
                    return input.fromUserClaims(claims ->
                            AddDeviceRequest.builder()
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
                        serviceComponent.provideAddDeviceActivity().handleRequest(request)
        );
    }
}
