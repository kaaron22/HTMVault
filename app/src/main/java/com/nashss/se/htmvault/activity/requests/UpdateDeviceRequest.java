package com.nashss.se.htmvault.activity.requests;

public class UpdateDeviceRequest {
    private final String serialNumber;
    private final String manufacturer;
    private final String model;
    private final String manufactureDate;
    private final String facilityName;
    private final String assignedDepartment;
    private final String nextPmDueDate;
    private final String notes;

    private UpdateDeviceRequest(String serialNumber, String manufacturer, String model, String manufactureDate,
                                String facilityName, String assignedDepartment, String nextPmDueDate, String notes) {
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.manufactureDate = manufactureDate;
        this.facilityName = facilityName;
        this.assignedDepartment = assignedDepartment;
        this.nextPmDueDate = nextPmDueDate;
        this.notes = notes;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public String getNextPmDueDate() {
        return nextPmDueDate;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "UpdateDeviceRequest{" +
                "serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + manufactureDate + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", nextPmDueDate='" + nextPmDueDate + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String serialNumber;
        private String manufacturer;
        private String model;
        private String manufactureDate;
        private String facilityName;
        private String assignedDepartment;
        private String nextPmDueDate;
        private String notes;

        public Builder withserialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
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

        public Builder withManufactureDate(String manufactureDate) {
            this.manufactureDate = manufactureDate;
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

        public Builder withNextPmDueDate(String nextPmDueDate) {
            this.nextPmDueDate = nextPmDueDate;
            return this;
        }

        public Builder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public UpdateDeviceRequest build() {
            return new UpdateDeviceRequest(serialNumber, manufacturer, model, manufactureDate, facilityName,
                    assignedDepartment, nextPmDueDate, notes);
        }
    }
}
