package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CreateWorkOrderRequest.Builder.class)
public class CreateWorkOrderRequest {

    private final String controlNumber;
    private final String workOrderType;
    private final String problemReported;
    private final String problemFound;

    private CreateWorkOrderRequest(String controlNumber, String workOrderType, String problemReported,
                                   String problemFound) {
        this.controlNumber = controlNumber;
        this.workOrderType = workOrderType;
        this.problemReported = problemReported;
        this.problemFound = problemFound;
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

    @Override
    public String toString() {
        return "CreateWorkOrderRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", workOrderType='" + workOrderType + '\'' +
                ", problemReported='" + problemReported + '\'' +
                ", problemFound='" + problemFound + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String controlNumber;
        private String workOrderType;
        private String problemReported;
        private String problemFound;

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

        public CreateWorkOrderRequest build() {
            return new CreateWorkOrderRequest(controlNumber, workOrderType, problemReported, problemFound);
        }
    }
}
