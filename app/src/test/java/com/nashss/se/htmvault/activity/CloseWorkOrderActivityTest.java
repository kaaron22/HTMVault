package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.CloseWorkOrderNotCompleteException;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderAwaitStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CloseWorkOrderActivityTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private WorkOrderDao workOrderDaoMaintenanceStatUpdates;
    @Mock
    private DeviceDao deviceDaoMaintenanceStatUpdates;

    private CloseWorkOrderActivity closeWorkOrderActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        WorkOrderDao workOrderDao = new WorkOrderDao(dynamoDBMapper, metricsPublisher);
        DeviceDao deviceDao = new DeviceDao(dynamoDBMapper, metricsPublisher);
        closeWorkOrderActivity = new CloseWorkOrderActivity(workOrderDao, deviceDao, metricsPublisher);
    }

    @Test
    public void handleRequest_workOrderFullyDocumented_returnsUpdatedWorkOrderInResult() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .withCustomerId("123")
                .withCustomerName("betty biomed")
                .build();
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);
        doNothing().when(dynamoDBMapper).save(any(WorkOrder.class));

        WorkOrder expectedWorkOrder = copyWorkOrder(workOrder);
        expectedWorkOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        expectedWorkOrder.setClosedById("123");
        expectedWorkOrder.setClosedByName("betty biomed");
        expectedWorkOrder.setWorkOrderAwaitStatus(null);

        // WHEN
        CloseWorkOrderResult closeWorkOrderResult = closeWorkOrderActivity.handleRequest(closeWorkOrderRequest);
        WorkOrderModel workOrderModel = closeWorkOrderResult.getWorkOrder();
        expectedWorkOrder.setClosedDateTime(workOrder.getClosedDateTime());

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(expectedWorkOrder, workOrderModel);
    }

    @Test
    public void handleRequest_workOrderNotFound_throwsWorkOrderNotFoundException() {
        // GIVEN
        CloseWorkOrderRequest closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .build();
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(null);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

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
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

        // WHEN & THEN
        assertThrows(CloseWorkOrderNotCompleteException.class, () ->
                        closeWorkOrderActivity.handleRequest(closeWorkOrderRequest),
                "Expected a request to close a work order that has a null 'completion date time' to result " +
                        "in a CloseWorkOrderNotCompleteException thrown");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_workOrderNotFound_throwsWorkOrderNotFoundException() {
        // GIVEN
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(WorkOrderNotFoundException.class, () ->
                closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123"),
                "Expected attempting to advance maintenance stats with a work order ID not found to result " +
                        "in a WorkOrderNotFoundException thrown");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(new WorkOrder());
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                        closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123"),
                "Expected attempting to advance maintenance stats with a work order ID references a device " +
                        "that is not found to result in a DeviceNotFoundException thrown");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_workOrderOpen_returnsOriginalDevice() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_repairWorkOrder_returnsOriginalDevice() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.REPAIR);

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_noMaintenanceRequired_returnsDeviceUpdatedLastPmOnly() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_rollsBackComplianceDate_retainsOriginalCompliance() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-04-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/15/2023, so the next pm and compliance-through-date
        // are 6/30/2024. we are attempting to update maintenance stats with a pm completed 4/15/2023,
        // which would cause the compliance-through-date to roll back, so we expect it should not
        // proceed to do so
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        device.setLastPmCompletionDate(LocalDate.of(2023, 6, 15));
        device.setNextPmDueDate(LocalDate.of(2024, 6, 30));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_willAdvanceComplianceDate_updatesCompliance() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/15/2022, so the next pm and compliance-through-date
        // are 6/30/2023. we are attempting to update maintenance stats with a pm completed 6/15/2023,
        // which would cause the compliance-through-date to advance, so we expect it should update to
        // 6/30/2024
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 6, 30));
        device.setLastPmCompletionDate(LocalDate.of(2022, 6, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 6, 30));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        copyDevice.setNextPmDueDate(LocalDate.of(2024, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_nullComplianceDate_updatesCompliance() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/15/2022, so the next pm is due 6/30/2023, but the
        // compliance-through-date is null. we are attempting to update maintenance stats with a pm
        // completed 6/15/2023, which would cause the compliance-through-date to advance, so we expect
        // it should update t0 6/30/2024
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(LocalDate.of(2022, 6, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 6, 30));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        copyDevice.setNextPmDueDate(LocalDate.of(2024, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_notScheduled_syncsNextPmWithCompliance() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/15/2022, so the compliance-through-date is 6/30/2023,
        // but there is no next pm due date. we are attempting to update maintenance stats with a pm
        // completed 6/15/2023, which would cause the compliance-through-date to advance, so we expect
        // it should update to 6/30/2024 and update the next pm due date to the same
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 6, 30));
        device.setLastPmCompletionDate(LocalDate.of(2022, 6, 12));
        device.setNextPmDueDate(null);

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        copyDevice.setNextPmDueDate(LocalDate.of(2024, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_nextCyclePmWithinCompliance_updatesNextPm() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-02-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 1/31/2022, so the compliance-through-date and next pm due
        // is 1/31/2023. we are attempting to update maintenance stats with a pm completed 2/15/2023,
        // which would cause the compliance-through-date to advance to 2/29/2024, but the next pm should
        // remain on the normal yearly schedule of every january
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 1, 31));
        device.setLastPmCompletionDate(LocalDate.of(2022, 1, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 1, 31));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2024, 2, 29));
        copyDevice.setNextPmDueDate(LocalDate.of(2024, 1, 31));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 2, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result);
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

    private Device copyDevice(Device device) {
        Device deviceCopy = new Device();
        deviceCopy.setControlNumber(device.getControlNumber());
        deviceCopy.setSerialNumber(device.getSerialNumber());
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(device.getManufacturerModel().getManufacturer());
        manufacturerModel.setModel(device.getManufacturerModel().getModel());
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(device.getManufacturerModel()
                .getRequiredMaintenanceFrequencyInMonths());
        deviceCopy.setManufactureDate(device.getManufactureDate());
        deviceCopy.setManufacturerModel(manufacturerModel);
        deviceCopy.setServiceStatus(device.getServiceStatus());
        deviceCopy.setFacilityName(device.getFacilityName());
        deviceCopy.setAssignedDepartment(device.getAssignedDepartment());
        deviceCopy.setComplianceThroughDate(device.getComplianceThroughDate());
        deviceCopy.setLastPmCompletionDate(device.getLastPmCompletionDate());
        deviceCopy.setNextPmDueDate(device.getNextPmDueDate());
        deviceCopy.setInventoryAddDate(device.getInventoryAddDate());
        deviceCopy.setAddedById(device.getAddedById());
        deviceCopy.setAddedByName(device.getAddedByName());
        deviceCopy.setNotes(device.getNotes());

        return deviceCopy;
    }
}