package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.converters.ManufacturerModelConverter;
import com.nashss.se.htmvault.models.WorkOrderAwaitStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

import java.time.LocalDateTime;
import java.util.Objects;

@DynamoDBTable(tableName = "work_orders")
public class WorkOrder {

    public static final String CONTROL_NUMBER_WORK_ORDERS_INDEX = "ControlNumberWorkOrdersIndex";

    private String workOrderId;
    private WorkOrderType workOrderType;
    private String controlNumber;
    private String serialNumber;
    private WorkOrderCompletionStatus workOrderCompletionStatus;
    private WorkOrderAwaitStatus workOrderAwaitStatus;
    private ManufacturerModel manufacturerModel;
    private String facilityName;
    private String assignedDepartment;
    private String problemReported;
    private String problemFound;
    private String createdById;
    private String createdByName;
    private LocalDateTime creationDateTime;
    private String closedById;
    private String closedByName;
    private LocalDateTime closedDateTime;
    private String summary;
    private LocalDateTime completionDateTime;

    @DynamoDBHashKey(attributeName = "workOrderId")
    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "workOrderType")
    public WorkOrderType getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(WorkOrderType workOrderType) {
        this.workOrderType = workOrderType;
    }

    @DynamoDBIndexHashKey(attributeName = "controlNumber", globalSecondaryIndexName = CONTROL_NUMBER_WORK_ORDERS_INDEX)
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

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "workOrderCompletionStatus")
    public WorkOrderCompletionStatus getWorkOrderCompletionStatus() {
        return workOrderCompletionStatus;
    }

    public void setWorkOrderCompletionStatus(WorkOrderCompletionStatus workOrderCompletionStatus) {
        this.workOrderCompletionStatus = workOrderCompletionStatus;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "workOrderAwaitStatus")
    public WorkOrderAwaitStatus getWorkOrderAwaitStatus() {
        return workOrderAwaitStatus;
    }

    public void setWorkOrderAwaitStatus(WorkOrderAwaitStatus workOrderAwaitStatus) {
        this.workOrderAwaitStatus = workOrderAwaitStatus;
    }

    @DynamoDBTypeConverted(converter = ManufacturerModelConverter.class)
    @DynamoDBAttribute(attributeName = "manufacturerModel")
    public ManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }

    public void setManufacturerModel(ManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
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

    @DynamoDBAttribute(attributeName = "problemReported")
    public String getProblemReported() {
        return problemReported;
    }

    public void setProblemReported(String problemReported) {
        this.problemReported = problemReported;
    }

    @DynamoDBAttribute(attributeName = "problemFound")
    public String getProblemFound() {
        return problemFound;
    }

    public void setProblemFound(String problemFound) {
        this.problemFound = problemFound;
    }

    @DynamoDBAttribute(attributeName = "createdById")
    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @DynamoDBAttribute(attributeName = "createdByName")
    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "creationDateTime")
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    @DynamoDBAttribute(attributeName = "closedById")
    public String getClosedById() {
        return closedById;
    }

    public void setClosedById(String closedById) {
        this.closedById = closedById;
    }

    @DynamoDBAttribute(attributeName = "closedByName")
    public String getClosedByName() {
        return closedByName;
    }

    public void setClosedByName(String closedByName) {
        this.closedByName = closedByName;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "closedDateTime")
    public LocalDateTime getClosedDateTime() {
        return closedDateTime;
    }

    public void setClosedDateTime(LocalDateTime closedDateTime) {
        this.closedDateTime = closedDateTime;
    }

    @DynamoDBAttribute(attributeName = "summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "")
    public LocalDateTime getCompletionDateTime() {
        return completionDateTime;
    }

    @DynamoDBAttribute(attributeName = "completionDateTime")
    public void setCompletionDateTime(LocalDateTime completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkOrder workOrder = (WorkOrder) o;
        return Objects.equals(workOrderId, workOrder.workOrderId) &&
                workOrderType == workOrder.workOrderType &&
                Objects.equals(controlNumber, workOrder.controlNumber) &&
                Objects.equals(serialNumber, workOrder.serialNumber) &&
                workOrderCompletionStatus == workOrder.workOrderCompletionStatus &&
                workOrderAwaitStatus == workOrder.workOrderAwaitStatus &&
                Objects.equals(manufacturerModel, workOrder.manufacturerModel) &&
                Objects.equals(facilityName, workOrder.facilityName) &&
                Objects.equals(assignedDepartment, workOrder.assignedDepartment) &&
                Objects.equals(problemReported, workOrder.problemReported) &&
                Objects.equals(problemFound, workOrder.problemFound) &&
                Objects.equals(createdById, workOrder.createdById) &&
                Objects.equals(createdByName, workOrder.createdByName) &&
                Objects.equals(creationDateTime, workOrder.creationDateTime) &&
                Objects.equals(closedById, workOrder.closedById) &&
                Objects.equals(closedByName, workOrder.closedByName) &&
                Objects.equals(closedDateTime, workOrder.closedDateTime) &&
                Objects.equals(summary, workOrder.summary) &&
                Objects.equals(completionDateTime, workOrder.completionDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workOrderId, workOrderType, controlNumber, serialNumber, workOrderCompletionStatus,
                workOrderAwaitStatus, manufacturerModel, facilityName, assignedDepartment, problemReported,
                problemFound, createdById, createdByName, creationDateTime, closedById, closedByName, closedDateTime,
                summary, completionDateTime);
    }
}
