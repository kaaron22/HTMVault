package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = AddDeviceRequest.Builder.class)
public class AddDeviceRequest {

    private final String controlNumber;
    private final String serialNumber;
    private final String manufacturer;
    private final String model;
    private final String manufactureDate;
    private final String facilityName;
    private final String assignedDepartment;
    private final int maintenanceFrequencyInMonths;
    private final String notes;

    public AddDeviceRequest(String controlNumber, String serialNumber, String manufacturer, String model,
                            String manufactureDate, String facilityName, String assignedDepartment,
                            int maintenanceFrequencyInMonths, String notes) {
        this.controlNumber = controlNumber;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.manufactureDate = manufactureDate;
        this.facilityName = facilityName;
        this.assignedDepartment = assignedDepartment;
        this.maintenanceFrequencyInMonths = maintenanceFrequencyInMonths;
        this.notes = notes;
    }

    public String getControlNumber() {
        return controlNumber;
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

    public int getMaintenanceFrequencyInMonths() {
        return maintenanceFrequencyInMonths;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "AddDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + manufactureDate + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", maintenanceFrequencyInMonths=" + maintenanceFrequencyInMonths +
                ", notes='" + notes + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private String controlNumber;
        private String serialNumber;
        private String manufacturer;
        private String model;
        private String manufactureDate;
        private String facilityName;
        private String assignedDepartment;
        private int maintenanceFrequencyInMonths;
        private String notes;

        Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }


    }
}
