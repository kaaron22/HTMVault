package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.converters.WorkOrderSummaryListConverter;

import java.time.LocalDate;
import java.util.List;

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
    private List<WorkOrderSummary> workOrders;

    @DynamoDBHashKey(attributeName = "controlNumber")
    public String getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    @DynamoDBAttribute(attributeName = "serialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @DynamoDBAttribute(attributeName = "manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @DynamoDBAttribute(attributeName = "model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "manufactureDate")
    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    @DynamoDBAttribute(attributeName = "serviceStatus")
    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @DynamoDBAttribute(attributeName = "facilityName")
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @DynamoDBAttribute(attributeName = "assignedDepartment")
    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "complianceThroughDate")
    public LocalDate getComplianceThroughDate() {
        return complianceThroughDate;
    }

    public void setComplianceThroughDate(LocalDate complianceThroughDate) {
        this.complianceThroughDate = complianceThroughDate;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "lastPmCompletionDate")
    public LocalDate getLastPmCompletionDate() {
        return lastPmCompletionDate;
    }

    public void setLastPmCompletionDate(LocalDate lastPmCompletionDate) {
        this.lastPmCompletionDate = lastPmCompletionDate;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "nextPmDueDate")
    public LocalDate getNextPmDueDate() {
        return nextPmDueDate;
    }

    public void setNextPmDueDate(LocalDate nextPmDueDate) {
        this.nextPmDueDate = nextPmDueDate;
    }

    @DynamoDBAttribute(attributeName = "maintenanceFrequencyInMonths")
    public Integer getMaintenanceFrequencyInMonths() {
        return maintenanceFrequencyInMonths;
    }

    public void setMaintenanceFrequencyInMonths(Integer maintenanceFrequencyInMonths) {
        this.maintenanceFrequencyInMonths = maintenanceFrequencyInMonths;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "inventoryAddDate")
    public LocalDate getInventoryAddDate() {
        return inventoryAddDate;
    }

    public void setInventoryAddDate(LocalDate inventoryAddDate) {
        this.inventoryAddDate = inventoryAddDate;
    }

    @DynamoDBAttribute(attributeName = "addedById")
    public String getAddedById() {
        return addedById;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    @DynamoDBAttribute(attributeName = "addedByName")
    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    @DynamoDBAttribute(attributeName = "notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @DynamoDBTypeConverted(converter = WorkOrderSummaryListConverter.class)
    @DynamoDBAttribute(attributeName = "workOrders")
    public List<WorkOrderSummary> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<WorkOrderSummary> workOrders) {
        this.workOrders = workOrders;
    }
}
