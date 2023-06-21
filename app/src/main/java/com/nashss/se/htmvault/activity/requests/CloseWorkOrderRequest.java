package com.nashss.se.htmvault.activity.requests;

public class CloseWorkOrderRequest {

    private final String workOrderId;
    private final String customerId;
    private final String customerName;

    private CloseWorkOrderRequest(String workOrderId, String customerId, String customerName) {
        this.workOrderId = workOrderId;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "CloseWorkOrderRequest{" +
                "workOrderId='" + workOrderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String workOrderId;
        private String customerId;
        private String customerName;

        public Builder withWorkOrderId(String workOrderId) {
            this.workOrderId = workOrderId;
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

        public CloseWorkOrderRequest build() {
            return new CloseWorkOrderRequest(workOrderId, customerId, customerName);
        }
    }
}
