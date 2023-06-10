package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ReactivateDeviceRequest.Builder.class)
public class ReactivateDeviceRequest {

    private final String controlNumber;
    private final String customerId;
    private final String customerName;

    private ReactivateDeviceRequest(String controlNumber, String customerId, String customerName) {
        this.controlNumber = controlNumber;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "ReactivateDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String controlNumber;
        private String customerId;
        private String customerName;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public ReactivateDeviceRequest build() {
            return new ReactivateDeviceRequest(controlNumber, customerId, customerName);
        }
    }
}
