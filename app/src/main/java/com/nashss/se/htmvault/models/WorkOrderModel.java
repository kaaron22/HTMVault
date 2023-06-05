package com.nashss.se.htmvault.models;

import java.util.Objects;

public class WorkOrderModel {

    private final String workOrderId;
    private final String controlNumber;
    private final String serialNumber;
    private final String completionStatus;
    private final String awaitStatus;
    private final String manufacturer;
    private final String model;
    private final String facilityName;
    private final String assignedDepartment;
    private final String problemReported;
    private final String problemFound;
    private final String createdById;
    private final String createdByName;
    private final String creationDateTime;
    private final String closedById;
    private final String closedByName;
    private final String closedDateTime;
    private final String summary;
    private final String completionDateTime;

    private WorkOrderModel(String workOrderId, String controlNumber, String serialNumber, String completionStatus,
                          String awaitStatus, String manufacturer, String model, String facilityName,
                          String assignedDepartment, String problemReported, String problemFound, String createdById,
                          String createdByName, String creationDateTime, String closedById, String closedByName,
                          String closedDateTime, String summary, String completionDateTime) {
        this.workOrderId = workOrderId;
        this.controlNumber = controlNumber;
        this.serialNumber = serialNumber;
        this.completionStatus = completionStatus;
        this.awaitStatus = awaitStatus;
        this.manufacturer = manufacturer;
        this.model = model;
        this.facilityName = facilityName;
        this.assignedDepartment = assignedDepartment;
        this.problemReported = problemReported;
        this.problemFound = problemFound;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.creationDateTime = creationDateTime;
        this.closedById = closedById;
        this.closedByName = closedByName;
        this.closedDateTime = closedDateTime;
        this.summary = summary;
        this.completionDateTime = completionDateTime;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public String getAwaitStatus() {
        return awaitStatus;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
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

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public String getClosedById() {
        return closedById;
    }

    public String getClosedByName() {
        return closedByName;
    }

    public String getClosedDateTime() {
        return closedDateTime;
    }

    public String getSummary() {
        return summary;
    }

    public String getCompletionDateTime() {
        return completionDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkOrderModel that = (WorkOrderModel) o;
        return Objects.equals(workOrderId, that.workOrderId) &&
                Objects.equals(controlNumber, that.controlNumber) &&
                Objects.equals(serialNumber, that.serialNumber) &&
                Objects.equals(completionStatus, that.completionStatus) &&
                Objects.equals(awaitStatus, that.awaitStatus) &&
                Objects.equals(manufacturer, that.manufacturer) &&
                Objects.equals(model, that.model) &&
                Objects.equals(facilityName, that.facilityName) &&
                Objects.equals(assignedDepartment, that.assignedDepartment) &&
                Objects.equals(problemReported, that.problemReported) &&
                Objects.equals(problemFound, that.problemFound) &&
                Objects.equals(createdById, that.createdById) &&
                Objects.equals(createdByName, that.createdByName) &&
                Objects.equals(creationDateTime, that.creationDateTime) &&
                Objects.equals(closedById, that.closedById) &&
                Objects.equals(closedByName, that.closedByName) &&
                Objects.equals(closedDateTime, that.closedDateTime) &&
                Objects.equals(summary, that.summary) &&
                Objects.equals(completionDateTime, that.completionDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workOrderId, controlNumber, serialNumber, completionStatus, awaitStatus, manufacturer,
                model, facilityName, assignedDepartment, problemReported, problemFound, createdById, createdByName,
                creationDateTime, closedById, closedByName, closedDateTime, summary, completionDateTime);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String workOrderId;
        private String controlNumber;
        private String serialNumber;
        private String completionStatus;
        private String awaitStatus;
        private String manufacturer;
        private String model;
        private String facilityName;
        private String assignedDepartment;
        private String problemReported;
        private String problemFound;
        private String createdById;
        private String createdByName;
        private String creationDateTime;
        private String closedById;
        private String closedByName;
        private String closedDateTime;
        private String summary;
        private String completionDateTime;

        public Builder withWorkOrderId(String workOrderId) {
            this.workOrderId = workOrderId;
            return this;
        }
        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder withCompletionStatus(String completionStatus) {
            this.completionStatus = completionStatus;
            return this;
        }

        public Builder withAwaitStatus(String awaitStatus) {
            this.awaitStatus = awaitStatus;
            return this;
        }

        public Builder withManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withFacilityName(String facilityName) {
            this.facilityName = facilityName;
            return this;
        }

        public Builder withAssignedDepartment(String assignedDepartment) {
            this.assignedDepartment = assignedDepartment;
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

        public Builder withClosedById(String closedById) {
            this.closedById = closedById;
            return this;
        }

        public Builder withClosedByName(String closedByName) {
            this.closedByName = closedByName;
            return this;
        }

        public Builder withClosedDateTime(String closedDateTime) {
            this.closedDateTime = closedDateTime;
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

        public WorkOrderModel build() {
            return new WorkOrderModel(workOrderId, controlNumber, serialNumber, completionStatus, awaitStatus,
                    manufacturer, model, facilityName, assignedDepartment, problemReported, problemFound, createdById,
                    createdByName, creationDateTime, closedById, closedByName, closedDateTime, summary,
                    completionDateTime);
        }
    }
}
