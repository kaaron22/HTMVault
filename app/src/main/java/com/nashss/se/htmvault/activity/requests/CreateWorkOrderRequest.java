package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CreateWorkOrderRequest.Builder.class)
public class CreateWorkOrderRequest {

    private final String controlNumber;
    private final String workOrderType;
    private final String problemReported;
    private final String problemFound;
    private final String createdById;
    private final String createdByName;
    private final String sortOrder;

    private CreateWorkOrderRequest(String controlNumber, String workOrderType, String problemReported,
                                   String problemFound, String createdById, String createdByName,
                                   String sortOrder) {
        this.controlNumber = controlNumber;
        this.workOrderType = workOrderType;
        this.problemReported = problemReported;
        this.problemFound = problemFound;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.sortOrder = sortOrder;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public String getProblemReported() {
        return problemReported;
    }

    public String getProblemFound() {
        return problemFound;
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return "CreateWorkOrderRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", workOrderType='" + workOrderType + '\'' +
                ", problemReported='" + problemReported + '\'' +
                ", problemFound='" + problemFound + '\'' +
                ", createdById='" + createdById + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String controlNumber;
        private String workOrderType;
        private String problemReported;
        private String problemFound;
        private String createdById;
        private String createdByName;
        private String sortOrder;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withWorkOrderType(String workOrderType) {
            this.workOrderType = workOrderType;
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

        public Builder withCreatedById(String createdById) {
            this.createdById = createdById;
            return this;
        }

        public Builder withCreatedByName(String createdByName) {
            this.createdByName = createdByName;
            return this;
        }

        public Builder withSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public CreateWorkOrderRequest build() {
            return new CreateWorkOrderRequest(controlNumber, workOrderType, problemReported, problemFound,
                    createdById, createdByName, sortOrder);
        }
    }
}
