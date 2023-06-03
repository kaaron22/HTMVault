package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.converters.ManufacturerModelConverter;
import com.nashss.se.htmvault.converters.WorkOrderSummaryListConverter;
import com.nashss.se.htmvault.models.ServiceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.nashss.se.htmvault.utils.CollectionUtils.copyToSet;

@DynamoDBTable(tableName = "devices")
public class Device {

    public static final String FACILITY_DEPARTMENT_INDEX = "FacilityNameAndAssignedDepartmentIndex";
    public static final String FACILITY_MANUFACTURER_MODEL_INDEX = "FacilityNameAndManufacturerModelIndex";
    public static final String FACILITY_PM_DUE_DATE_INDEX = "FacilityNameAndPmDueDateIndex";
    public static final String MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX = "ManufacturerModelAndSerialNumberIndex";

    private String controlNumber;
    private String serialNumber;
    private ManufacturerModel manufacturerModel;
    private LocalDate manufactureDate;
    private ServiceStatus serviceStatus;
    private String facilityName;
    private String assignedDepartment;
    private LocalDate complianceThroughDate;
    private LocalDate lastPmCompletionDate;
    private LocalDate nextPmDueDate;
    private LocalDate inventoryAddDate;
    private String addedById;
    private String addedByName;
    private String notes;
    private Set<String> workOrders;

    @DynamoDBHashKey(attributeName = "controlNumber")
    public String getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(String controlNumber) {
        this.controlNumber = controlNumber;
    }

    @DynamoDBAttribute(attributeName = "serialNumber")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @DynamoDBTypeConverted(converter = ManufacturerModelConverter.class)
    @DynamoDBAttribute(attributeName = "manufacturerModel")
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = FACILITY_MANUFACTURER_MODEL_INDEX)
    public ManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }

    public void setManufacturerModel(ManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }

    @DynamoDBTypeConverted(converter = LocalDateConverter.class)
    @DynamoDBAttribute(attributeName = "manufactureDate")
    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "serviceStatus")
    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    @DynamoDBAttribute(attributeName = "facilityName")
    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {FACILITY_DEPARTMENT_INDEX, FACILITY_MANUFACTURER_MODEL_INDEX,
            FACILITY_PM_DUE_DATE_INDEX})
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @DynamoDBAttribute(attributeName = "assignedDepartment")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = FACILITY_DEPARTMENT_INDEX)
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
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = FACILITY_PM_DUE_DATE_INDEX)
    public LocalDate getNextPmDueDate() {
        return nextPmDueDate;
    }

    public void setNextPmDueDate(LocalDate nextPmDueDate) {
        this.nextPmDueDate = nextPmDueDate;
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

    @DynamoDBAttribute(attributeName = "workOrders")
    public Set<String> getWorkOrders() {
        if (null == workOrders) {
            return null;
        }
        return copyToSet(workOrders);
    }

    public void setWorkOrders(Set<String> workOrders) {
        this.workOrders = workOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(controlNumber, device.controlNumber) &&
                Objects.equals(serialNumber, device.serialNumber) &&
                Objects.equals(manufacturerModel, device.manufacturerModel) &&
                Objects.equals(manufactureDate, device.manufactureDate) &&
                Objects.equals(serviceStatus, device.serviceStatus) &&
                Objects.equals(facilityName, device.facilityName) &&
                Objects.equals(assignedDepartment, device.assignedDepartment) &&
                Objects.equals(complianceThroughDate, device.complianceThroughDate) &&
                Objects.equals(lastPmCompletionDate, device.lastPmCompletionDate) &&
                Objects.equals(nextPmDueDate, device.nextPmDueDate) &&
                Objects.equals(inventoryAddDate, device.inventoryAddDate) &&
                Objects.equals(addedById, device.addedById) &&
                Objects.equals(addedByName, device.addedByName) &&
                Objects.equals(notes, device.notes) &&
                Objects.equals(workOrders, device.workOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlNumber, serialNumber, manufacturerModel, manufactureDate, serviceStatus,
                facilityName, assignedDepartment, complianceThroughDate, lastPmCompletionDate, nextPmDueDate,
                inventoryAddDate, addedById, addedByName, notes, workOrders);
    }
}
