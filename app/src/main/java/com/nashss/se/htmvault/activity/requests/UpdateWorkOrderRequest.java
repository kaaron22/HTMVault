package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = UpdateWorkOrderRequest.Builder.class)
public class UpdateWorkOrderRequest {

    private final String workOrderId;
    private final String workOrderType;
    private final String workOrderAwaitStatus;
    private final String problemReported;
    private final String problemFound;
    private final String summary;
    private final String completionDateTime;
    private final String customerId;
    private final String customerName;

    private UpdateWorkOrderRequest(String workOrderId, String workOrderType, String workOrderAwaitStatus,
                                   String problemReported, String problemFound, String summary,
                                   String completionDateTime, String customerId, String customerName) {
        this.workOrderId = workOrderId;
        this.workOrderType = workOrderType;
        this.workOrderAwaitStatus = workOrderAwaitStatus;
        this.problemReported = problemReported;
        this.problemFound = problemFound;
        this.summary = summary;
        this.completionDateTime = completionDateTime;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getWorkOrderId() {
        return workOrderId;
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

    @Override
    public String toString() {
        return "UpdateWorkOrderRequest{" +
                "workOrderId='" + workOrderId + '\'' +
                ", workOrderType='" + workOrderType + '\'' +
                ", workOrderAwaitStatus='" + workOrderAwaitStatus + '\'' +
                ", problemReported='" + problemReported + '\'' +
                ", problemFound='" + problemFound + '\'' +
                ", summary='" + summary + '\'' +
                ", completionDateTime='" + completionDateTime + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String workOrderId;
        private String workOrderType;
        private String workOrderAwaitStatus;
        private String problemReported;
        private String problemFound;
        private String summary;
        private String completionDateTime;
        private String customerId;
        private String customerName;

        public Builder withWorkOrderId(String workOrderId) {
            this.workOrderId = workOrderId;
            return this;
        }

        public Builder withWorkOrderType(String workOrderType) {
            this.workOrderType = workOrderType;
            return this;
        }

        public Builder withWorkOrderAwaitStatus(String workOrderAwaitStatus) {
            this.workOrderAwaitStatus = workOrderAwaitStatus;
            return this;
        }

        public Builder withProblemReported(String problemReported) {
            this.problemReported = problemReported;
            return this;
        }

        public Builder withProblemFound(String problemFound) {
            this.problemFound = problemFound;
            return this;
        }

        public Builder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder withCompletionDateTime(String completionDateTime) {
            this.completionDateTime = completionDateTime;
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

        public UpdateWorkOrderRequest build() {
            return new UpdateWorkOrderRequest(workOrderId, workOrderType, workOrderAwaitStatus, problemReported,
                    problemFound, summary, completionDateTime, customerId, customerName);
        }
    }
}
