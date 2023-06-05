package com.nashss.se.htmvault.activity.requests;

import com.nashss.se.htmvault.models.SortOrder;

public class GetDeviceWorkOrdersRequest {

    private final String controlNumber;
    private final String sortOrder;

    private GetDeviceWorkOrdersRequest(String controlNumber, String sortOrder) {
        this.controlNumber = controlNumber;
        this.sortOrder = sortOrder;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public String getSortOrder() {
        return sortOrder;
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
        private String sortOrder;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public GetDeviceWorkOrdersRequest build() {
            return new GetDeviceWorkOrdersRequest(controlNumber, sortOrder);
        }
    }
}
