package com.nashss.se.htmvault.activity.requests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AddDeviceRequest.Builder.class)
public class AddDeviceRequest {

    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String manufactureDate;
    private String facilityName;
    private String assignedDepartment;
    private int maintenanceFrequencyInMonths;
    private String notes;

    private AddDeviceRequest()
}
