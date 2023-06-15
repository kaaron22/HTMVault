package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.CloseWorkOrderNotCompleteException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CloseWorkOrderActivityTest {

    @Mock
    private WorkOrderDao workOrderDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private CloseWorkOrderActivity closeWorkOrderActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        closeWorkOrderActivity = new CloseWorkOrderActivity(workOrderDao, metricsPublisher);
    }

    @Test
    public void handleRequest_workOrderNotFound_throwsWorkOrderNotFoundException() {
        // GIVEN
        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenThrow(WorkOrderNotFoundException.class);

        // WHEN & THEN
        assertThrows(WorkOrderNotFoundException.class, () ->
                closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that was not found to result in a " +
                        "WorkOrderNotFoundException thrown");
    }

    @Test
    public void handleRequest_workOrderAlreadyClosed_returnsWorkOrderInResult() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        WorkOrder expectedWorkOrder = copyWorkOrder(workOrder);

        // WHEN
        CloseWorkOrderResult closeWorkOrderResult = closeWorkOrderActivity.handleRequest(closeWorkOrderRequest);

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(expectedWorkOrder, closeWorkOrderResult.getWorkOrder());
    }

    @Test
    public void handleRequest_blankProblemFound_throwsCloseWorkOrderNotCompleteException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("  ");
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has a blank 'problem found' to result in a " +
                        "CloseWorkOrderNotCompleteException thrown");
    }

    @Test
    public void handleRequest_nullProblemFound_throwsCloseWorkOrderNotCompleteException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound(null);
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has a null 'problem found' to result in a " +
                        "CloseWorkOrderNotCompleteException thrown");
    }

    @Test
    public void handleRequest_emptySummary_throwsCloseWorkOrderNotCompleteException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("not empty");
        workOrder.setSummary("");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has an empty 'summary' to result in a " +
                        "CloseWorkOrderNotCompleteException thrown");
    }

    @Test
    public void handleRequest_nullSummary_throwsCloseWorkOrderNotCompleteException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("not empty");
        workOrder.setSummary(null);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has a null 'summary' to result in a " +
                        "CloseWorkOrderNotCompleteException thrown");
    }

    @Test
    public void handleRequest_nullCompletionDateTime_throwsCloseWorkOrderNotCompleteException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("not empty");
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(null);

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has a null 'completion date time' to result " +
                        "in a CloseWorkOrderNotCompleteException thrown");
    }



    private WorkOrder copyWorkOrder(WorkOrder workOrder) {
        WorkOrder copyWorkOrder = new WorkOrder();
        copyWorkOrder.setWorkOrderId(workOrder.getWorkOrderId());
        copyWorkOrder.setWorkOrderType(workOrder.getWorkOrderType());
        copyWorkOrder.setControlNumber(workOrder.getControlNumber());
        copyWorkOrder.setSerialNumber(workOrder.getSerialNumber());
        copyWorkOrder.setWorkOrderCompletionStatus(workOrder.getWorkOrderCompletionStatus());
        copyWorkOrder.setWorkOrderAwaitStatus(workOrder.getWorkOrderAwaitStatus());
        copyWorkOrder.setManufacturerModel(workOrder.getManufacturerModel());
        copyWorkOrder.setFacilityName(workOrder.getFacilityName());
        copyWorkOrder.setAssignedDepartment(workOrder.getAssignedDepartment());
        copyWorkOrder.setProblemReported(workOrder.getProblemReported());
        copyWorkOrder.setProblemFound(workOrder.getProblemFound());
        copyWorkOrder.setCreatedById(workOrder.getCreatedById());
        copyWorkOrder.setCreatedByName(workOrder.getCreatedByName());
        copyWorkOrder.setCreationDateTime(workOrder.getCreationDateTime());
        copyWorkOrder.setClosedById(workOrder.getClosedById());
        copyWorkOrder.setClosedByName(workOrder.getClosedByName());
        copyWorkOrder.setClosedDateTime(workOrder.getClosedDateTime());
        copyWorkOrder.setSummary(workOrder.getSummary());
        copyWorkOrder.setCompletionDateTime(workOrder.getCompletionDateTime());

        return copyWorkOrder;
    }
}