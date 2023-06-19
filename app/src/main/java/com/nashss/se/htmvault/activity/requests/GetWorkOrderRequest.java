package com.nashss.se.htmvault.activity.requests;

public class GetWorkOrderRequest {

    private final String workOrderId;

    private GetWorkOrderRequest(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    @Override
    public String toString() {
        return "GetWorkOrderRequest{" +
                "workOrderId='" + workOrderId + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String workOrderId;

        public Builder withWorkOrderId(String workOrderId) {
            this.workOrderId = workOrderId;
            return this;
        }

        public GetWorkOrderRequest build() {
            return new GetWorkOrderRequest(workOrderId);
        }
    }
}
