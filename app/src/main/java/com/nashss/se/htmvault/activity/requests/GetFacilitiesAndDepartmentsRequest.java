package com.nashss.se.htmvault.activity.requests;

public class GetFacilitiesAndDepartmentsRequest {

    private final String customerId;
    private final String customerName;

    private GetFacilitiesAndDepartmentsRequest(String customerId, String customerName) {
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
        return "GetFacilitiesAndDepartmentsRequest{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
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

        public GetFacilitiesAndDepartmentsRequest build() {
            return new GetFacilitiesAndDepartmentsRequest(customerId, customerName);
        }
    }
}
