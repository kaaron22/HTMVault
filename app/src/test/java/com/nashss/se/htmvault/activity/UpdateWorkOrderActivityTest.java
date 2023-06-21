package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.UpdateWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.UpdateClosedWorkOrderException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderAwaitStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void handleRequest_minimumRequiredRequestValues_returnsWorkOrderModelInResult() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setProblemReported("A reported 'problim' with a misspelling");

        // a request with the minimum attributes required (problem found, summary, completion date/time are null)
        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId(workOrder.getWorkOrderId())
                .withWorkOrderType("REPAIR")
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("A corrected reported problem without the misspelling")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        WorkOrder expectedWorkOrder = copyWorkOrder(workOrder);
        expectedWorkOrder.setWorkOrderType(WorkOrderType.REPAIR);
        expectedWorkOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_REPAIR);
        expectedWorkOrder.setProblemReported("A corrected reported problem without the misspelling");


        // WHEN
        UpdateWorkOrderResult result = updateWorkOrderActivity.handleRequest(updateWorkOrderRequest);
        WorkOrderModel workOrderModel = result.getWorkOrder();

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(expectedWorkOrder, workOrderModel);
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_changingAllValuesAllowedWithAcceptableUpdates_returnsWorkOrderModelInResult() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setProblemReported("A reported problim with a misspelling");
        workOrder.setProblemFound("A problem found after diagnoss with a misspelling");
        workOrder.setSummary("A summary of work performed to resolve the issue, but we forgot to document an " +
                "important detail");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-14T10:00:01"));

        // a request with the minimum attributes required (problem found, summary, completion date/time are null)
        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId(workOrder.getWorkOrderId())
                .withWorkOrderType("REPAIR")
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("A corrected reported problem without the misspelling")
                .withProblemFound("A problem found after diagnosis")
                .withSummary("An accurate summary of the work performed")
                .withCompletionDateTime("2023-06-14T11:00:01")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        WorkOrder expectedWorkOrder = copyWorkOrder(workOrder);
        expectedWorkOrder.setWorkOrderType(WorkOrderType.REPAIR);
        expectedWorkOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_REPAIR);
        expectedWorkOrder.setProblemReported("A corrected reported problem without the misspelling");
        expectedWorkOrder.setProblemFound("A problem found after diagnosis");
        expectedWorkOrder.setSummary("An accurate summary of the work performed");
        expectedWorkOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-14T11:00:01"));

        // WHEN
        UpdateWorkOrderResult result = updateWorkOrderActivity.handleRequest(updateWorkOrderRequest);
        WorkOrderModel workOrderModel = result.getWorkOrder();

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(expectedWorkOrder, workOrderModel);
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 0);
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
                "Expected request to update a work order with an invalid work order await status to result " +
                        "in an InvalidAttributeValueException thrown");
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
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("   ")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with a blank 'problem reported' to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_emptyProblemReported_throwsInvalidAttributeValueException() {
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
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an empty 'problem reported' to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_invalidCompletionDateTime_throwsInvalidAttributeValueException() {
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
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("A reported problem")
                .withCompletionDateTime("not a date")
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an invalid 'completion date time' to result in" +
                        " an InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_futureCompletionDateTime_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);

        UpdateWorkOrderRequest updateWorkOrderRequest = UpdateWorkOrderRequest.builder()
                .withWorkOrderId("Valid")
                .withWorkOrderType("REPAIR")
                .withWorkOrderAwaitStatus("AWAITING_REPAIR")
                .withProblemReported("A reported problem")
                .withCompletionDateTime(new LocalDateTimeConverter().convert(futureDateTime))
                .build();
        when(workOrderDao.getWorkOrder(anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateWorkOrderActivity.handleRequest(updateWorkOrderRequest),
                "Expected request to update a work order with an future 'completion date time' to result in" +
                        " an InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
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
