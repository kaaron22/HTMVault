package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = RetireDeviceRequest.Builder.class)
public class RetireDeviceRequest {

    private String controlNumber;

    private RetireDeviceRequest(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    @Override
    public String toString() {
        return "RetireDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String controlNumber;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public RetireDeviceRequest build() {
            return new RetireDeviceRequest(controlNumber);
        }
    }
}
