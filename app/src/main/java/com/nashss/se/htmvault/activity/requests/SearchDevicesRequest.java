package com.nashss.se.htmvault.activity.requests;

import com.nashss.se.htmvault.lambda.AuthenticatedLambdaRequest;

public class SearchDevicesRequest {

    private final String criteria;

    private SearchDevicesRequest(String criteria) {
        this.criteria = criteria;
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return "SearchDevicesRequest{" +
                "criteria='" + criteria + '\'' +
                '}';
    }


}
