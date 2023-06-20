package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.UpdateDeviceRequest;
import com.nashss.se.htmvault.activity.results.UpdateDeviceResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateDeviceLambda
        extends LambdaActivityRunner<UpdateDeviceRequest, UpdateDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<UpdateDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to update a device's editable information.
     *
     * @param input   The Lambda Function input, an authenticated request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<UpdateDeviceRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            () -> {
                // the unauthenticated request, after deserializing the json data received
                UpdateDeviceRequest unauthenticatedRequest = input.fromBody(UpdateDeviceRequest.class);

                // the final request, including authentication information
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
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideUpdateDeviceActivity().handleRequest(request)
        );
    }
}
