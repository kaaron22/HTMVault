package com.nashss.se.htmvault.activity.requests;

public class GetDeviceRequest {

    private final String controlNumber;

    private GetDeviceRequest(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    @Override
    public String toString() {
        return "GetDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String controlNumber;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public GetDeviceRequest build() {
            return new GetDeviceRequest(controlNumber);
        }
    }
}
