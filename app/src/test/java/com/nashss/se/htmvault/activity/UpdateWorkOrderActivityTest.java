package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.UpdateClosedWorkOrderException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class UpdateWorkOrderActivityTest {

    @Mock
    private WorkOrderDao workOrderDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    @InjectMocks
    private UpdateWorkOrderActivity updateWorkOrderActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void handleRequest_nullWorkOrderId_throwsInvalidAttributeValueException() {
        // GIVEN
        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId(null)
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with a null work order ID to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_blankWorkOrderId_throwsInvalidAttributeValueException() {
        // GIVEN
        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("   ")
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with a null work order ID to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_noWorkOrderFoundForWorkOrderId_throwsWorkOrderNotFoundException() {
        // GIVEN
        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("NotFound")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenThrow(WorkOrderNotFoundException.class);

        // WHEN & THEN
        assertThrows(WorkOrderNotFoundException.class, () ->
                updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order for a work order not found to result in a " +
                        "WorkOrderNotFoundException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
    }

    @Test
    public void handleRequest_attemptUpdatesToClosedWorkOrder_throwsUpdateClosedWorkOrderException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);

        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("NotFound")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(UpdateClosedWorkOrderException.class, () ->
                updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a closed work order to result in an " +
                        "UpdateClosedWorkOrderException thrown");
    }

    @Test
    public void handleRequest_invalidWorkOrderType_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("Valid")
                .withWorkOrderType("InvalidType")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an invalid work order type to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_invalidWorkOrderAwaitStatus_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("Valid")
                .withWorkOrderType("REPAIR")
                .withWorkOrderAwaitStatus("Invalid")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an invalid work order type to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_blankProblemReported_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("Valid")
                .withWorkOrderType("REPAIR")
                .withWorkOrderAwaitStatus("AWAITING_APPROVAL")
                .withProblemFound("   ")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an invalid work order type to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }


}