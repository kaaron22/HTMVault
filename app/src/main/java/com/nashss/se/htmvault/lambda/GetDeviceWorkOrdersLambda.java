package com.nashss.se.htmvault.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;

public class GetDeviceWorkOrdersLambda
        extends LambdaActivityRunner<GetDeviceWorkOrdersRequest, GetDeviceWorkOrdersResult>
        implements RequestHandler<LambdaRequest<GetDeviceWorkOrdersRequest>, LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(LambdaRequest<GetDeviceWorkOrdersRequest> input, Context context) {
        return null;
    }
}
