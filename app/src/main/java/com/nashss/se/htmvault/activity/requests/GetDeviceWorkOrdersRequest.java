package com.nashss.se.htmvault.activity.requests;

import com.nashss.se.htmvault.models.SortOrder;

public class GetDeviceWorkOrdersRequest {

    private final String controlNumber;
    private final SortOrder sortOrder;

    private GetDeviceWorkOrdersRequest(String controlNumber, SortOrder sortOrder) {
        this.controlNumber = controlNumber;
        this.sortOrder = sortOrder;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    @Override
    public String toString() {
        return "GetDeviceWorkOrdersRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String controlNumber;
        private SortOrder sortOrder;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withSortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public GetDeviceWorkOrdersRequest build() {
            return new GetDeviceWorkOrdersRequest(controlNumber, sortOrder);
        }
    }
}
