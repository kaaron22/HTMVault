package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddDeviceLambda
        extends LambdaActivityRunner<AddDeviceRequest, AddDeviceResult>
        implements RequestHandler<AuthenticatedLambdaRequest<AddDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request for adding a device.
     *
     * @param input   The Lambda Function input, an authenticated add device request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<AddDeviceRequest> input, Context context) {
        log.info("handleRequest");

        return super.runActivity(
            () -> {
                // the unauthenticated request, after deserializing the json data received
                AddDeviceRequest unauthenticatedRequest = input.fromBody(AddDeviceRequest.class);

                // the final request, including authentication information
                return input.fromUserClaims(claims ->
                    AddDeviceRequest.builder()
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
                serviceComponent.provideAddDeviceActivity().handleRequest(request)
        );
    }
}
