package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = UpdateWorkOrderRequest.Builder.class)
public class UpdateWorkOrderRequest {

    private final String workOrderType;
    private final String workOrderAwaitStatus;
    private final String problemReported;
    private final String problemFound;
    private final String summary;
    private final String completionDateTime;
    private final String customerId;
    private final String customerName;

    private UpdateWorkOrderRequest(String workOrderType, String workOrderAwaitStatus, String problemReported,
                                   String problemFound, String summary, String completionDateTime, String customerId,
                                   String customerName) {
        this.workOrderType = workOrderType;
        this.workOrderAwaitStatus = workOrderAwaitStatus;
        this.problemReported = problemReported;
        this.problemFound = problemFound;
        this.summary = summary;
        this.completionDateTime = completionDateTime;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public String getWorkOrderAwaitStatus() {
        return workOrderAwaitStatus;
    }

    public String getProblemReported() {
        return problemReported;
    }

    public String getProblemFound() {
        return problemFound;
    }

    public String getSummary() {
        return summary;
    }

    public String getCompletionDateTime() {
        return completionDateTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UpdateWorkOrderRequest{" +
                "workOrderType='" + workOrderType + '\'' +
                ", workOrderAwaitStatus='" + workOrderAwaitStatus + '\'' +
                ", problemReported='" + problemReported + '\'' +
                ", problemFound='" + problemFound + '\'' +
                ", summary='" + summary + '\'' +
                ", completionDateTime='" + completionDateTime + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    public static class Builder {
        private String workOrderType;
        private String workOrderAwaitStatus;
        private String problemReported;
        private String problemFound;
        private String summary;
        private String completionDateTime;
        private String customerId;
        private String customerName;

        public Builder withWorkOrderType(String workOrderType) {
            this.workOrderType = workOrderType;
            return this;
        }

        public Builder withWorkOrderAwaitStatus(String workOrderAwaitStatus) {
            this.workOrderAwaitStatus = workOrderAwaitStatus;
            return this;
        }
    }
}
