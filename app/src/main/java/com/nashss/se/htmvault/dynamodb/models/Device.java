package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.LocalDate;

@DynamoDBTable(tableName = "devices")
public class Device {

    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private LocalDate manufactureDate;
    private String serviceStatus;
    private String facilityName;
    private String assignedDepartment;
    private LocalDate complianceThroughDate;
    private LocalDate lastPmCompletionDate;
    private LocalDate nextPmDueDate;
    private Integer maintenanceFrequencyInMonths;
    private LocalDate inventoryAddDate;
    private String addedById;
    private String addedByName;
    private String notes;
    List<WorkOrderSummaryModel> workOrders;

}
