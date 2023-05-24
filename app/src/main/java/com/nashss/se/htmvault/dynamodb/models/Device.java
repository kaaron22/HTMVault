package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

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


    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public void setAssignedDepartment(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    public LocalDate getComplianceThroughDate() {
        return complianceThroughDate;
    }

    public void setComplianceThroughDate(LocalDate complianceThroughDate) {
        this.complianceThroughDate = complianceThroughDate;
    }

    public LocalDate getLastPmCompletionDate() {
        return lastPmCompletionDate;
    }

    public void setLastPmCompletionDate(LocalDate lastPmCompletionDate) {
        this.lastPmCompletionDate = lastPmCompletionDate;
    }

    public LocalDate getNextPmDueDate() {
        return nextPmDueDate;
    }

    public void setNextPmDueDate(LocalDate nextPmDueDate) {
        this.nextPmDueDate = nextPmDueDate;
    }

    public Integer getMaintenanceFrequencyInMonths() {
        return maintenanceFrequencyInMonths;
    }

    public void setMaintenanceFrequencyInMonths(Integer maintenanceFrequencyInMonths) {
        this.maintenanceFrequencyInMonths = maintenanceFrequencyInMonths;
    }

    public LocalDate getInventoryAddDate() {
        return inventoryAddDate;
    }

    public void setInventoryAddDate(LocalDate inventoryAddDate) {
        this.inventoryAddDate = inventoryAddDate;
    }

    public String getAddedById() {
        return addedById;
    }

    public void setAddedById(String addedById) {
        this.addedById = addedById;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<WorkOrderSummary> getWorkOrders() {
        return workOrders;
    }

    public void setWorkOrders(List<WorkOrderSummary> workOrders) {
        this.workOrders = workOrders;
    }
}
