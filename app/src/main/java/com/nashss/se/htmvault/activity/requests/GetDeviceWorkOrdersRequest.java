package com.nashss.se.htmvault.activity.requests;

public class GetDeviceWorkOrdersRequest {

    private final String controlNumber;

    private GetDeviceWorkOrdersRequest(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    @Override
    public String toString() {
        return "GetDeviceWorkOrdersRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String controlNumber;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public GetDeviceWorkOrdersRequest build() {
            return new GetDeviceWorkOrdersRequest(controlNumber);
        }
    }
}
