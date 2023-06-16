package com.nashss.se.htmvault.activity.requests;

public class GetManufacturersAndModelsRequest {

    private final String customerId;
    private final String customerName;

    private GetManufacturersAndModelsRequest(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "GetManufacturersAndModelsRequest{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String customerId;
        private String customerName;

        public Builder withCustomerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public GetManufacturersAndModelsRequest build() {
            return new GetManufacturersAndModelsRequest(customerId, customerName);
        }
    }
}
