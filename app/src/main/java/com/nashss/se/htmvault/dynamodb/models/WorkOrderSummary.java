package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;

import java.time.LocalDateTime;
import java.util.Objects;

@DynamoDBTable(tableName = "work_order_summaries")
public class WorkOrderSummary {

    private String workOrderId;
    private WorkOrderType workOrderType;
    private WorkOrderCompletionStatus completionStatus;
    private LocalDateTime dateTimeCreated;
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

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName = "completionStatus")
    public WorkOrderCompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(WorkOrderCompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "dateTimeCreated")
    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    @DynamoDBAttribute(attributeName = "completionDateTime")
    public LocalDateTime getCompletionDateTime() {
        return completionDateTime;
    }

    public void setCompletionDateTime(LocalDateTime completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkOrderSummary that = (WorkOrderSummary) o;
        return Objects.equals(workOrderId, that.workOrderId) &&
                Objects.equals(workOrderType, that.workOrderType) &&
                Objects.equals(completionStatus, that.completionStatus) &&
                Objects.equals(dateTimeCreated, that.dateTimeCreated) &&
                Objects.equals(completionDateTime, that.completionDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workOrderId, workOrderType, completionStatus, dateTimeCreated, completionDateTime);
    }
}
