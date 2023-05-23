package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
}
