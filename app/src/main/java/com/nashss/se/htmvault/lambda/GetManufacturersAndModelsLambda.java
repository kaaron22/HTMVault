package com.nashss.se.htmvault.lambda;

import com.nashss.se.htmvault.activity.requests.GetManufacturersAndModelsRequest;
import com.nashss.se.htmvault.activity.results.GetManufacturersAndModelsResult;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetManufacturersAndModelsLambda
        extends LambdaActivityRunner<GetManufacturersAndModelsRequest, GetManufacturersAndModelsResult>
        implements RequestHandler<AuthenticatedLambdaRequest<GetManufacturersAndModelsRequest>, LambdaResponse> {

    private final Logger log = LogManager.getLogger();

    /**
     * Handles a Lambda Function request to get a list of existing manufacturer/model objects.
     *
     * @param input   The Lambda Function input, an authenticated request
     * @param context The Lambda execution environment context object.
     * @return The Lambda Function output
     */
    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<GetManufacturersAndModelsRequest> input,
                                        Context context) {
        log.info("handleRequest");

        return super.runActivity(
            // the request, using authentication information
            () -> input.fromUserClaims(claims ->
                GetManufacturersAndModelsRequest.builder()
                    .withCustomerId(claims.get("email"))
                    .withCustomerName(claims.get("name"))
                    .build()),
            // the call to our activity
            (request, serviceComponent) ->
                serviceComponent.provideGetManufacturersAndModelsActivity().handleRequest(request)
        );
    }
}
