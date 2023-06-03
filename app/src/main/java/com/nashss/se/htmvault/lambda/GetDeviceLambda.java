package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.GetDeviceRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetDeviceLambda
        extends LambdaActivityRunner<GetDeviceRequest, GetDeviceResult>
        implements RequestHandler<LambdaRequest<GetDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<GetDeviceRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
                () -> input.fromPath(path ->
                            GetDeviceRequest.builder()
                                    .withControlNumber(path.get("controlNumber"))
                                    .build()),
                (request, serviceComponent) ->
                        serviceComponent.provideGetDeviceActivity().handleRequest(request)
        );
    }
}
