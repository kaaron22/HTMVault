package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetDeviceWorkOrdersLambda
        extends LambdaActivityRunner<GetDeviceWorkOrdersRequest, GetDeviceWorkOrdersResult>
        implements RequestHandler<LambdaRequest<GetDeviceWorkOrdersRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    @Override
    public LambdaResponse handleRequest(LambdaRequest<GetDeviceWorkOrdersRequest> input, Context context) {
        log.info("handleRequest");
        return super.runActivity(
                () -> input.fromPathAndQuery((path, query) ->
                        GetDeviceWorkOrdersRequest.builder()
                                .withControlNumber(path.get("controlNumber"))
                                .withSortOrder(query.get("order"))
                                .build()),
                (request, serviceComponent) ->
                        serviceComponent.provideGetDeviceWorkOrdersActivity().handleRequest(request)
        );
    }
}
