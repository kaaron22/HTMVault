package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class RetireDeviceLambda
        extends LambdaActivityRunner<RetireDeviceRequest, RetireDeviceResult>
        implements RequestHandler<LambdaRequest<RetireDeviceRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request
     *
     * @param input   The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(LambdaRequest<RetireDeviceRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
                () -> input.fromPath(path ->
                        RetireDeviceRequest.builder()
                                .withControlNumber(path.get("controlNumber"))
                                .build()),
                (request, serviceComponent) -> serviceComponent.provideRetireDeviceActivity().handleRequest(request)
        );
    }
}
