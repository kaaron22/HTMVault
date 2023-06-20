package com.nashss.se.htmvault.activity;

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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CloseWorkOrderActivityTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    private CloseWorkOrderActivity closeWorkOrderActivity;
    private CloseWorkOrderRequest closeWorkOrderRequest;
    private final ManufacturerModel manufacturerModel = new ManufacturerModel();
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        openMocks(this);
        WorkOrderDao workOrderDao = new WorkOrderDao(dynamoDBMapper, metricsPublisher);
        DeviceDao deviceDao = new DeviceDao(dynamoDBMapper, metricsPublisher);
        closeWorkOrderActivity = new CloseWorkOrderActivity(workOrderDao, deviceDao, metricsPublisher);

        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");

        workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        workOrder.setProblemFound("a problem found");
        workOrder.setSummary("a summary");

        closeWorkOrderRequest = CloseWorkOrderRequest.builder()
                .withWorkOrderId("a work order id")
                .withCustomerId("123")
                .withCustomerName("betty biomed")
                .build();
    }

    @Test
    public void handleRequest_workOrderFullyDocumented_returnsUpdatedWorkOrderInResult() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound("  ");
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setProblemFound(null);
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setSummary("");
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setSummary(null);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-06-15T10:00:01"));

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setSummary("not empty");
        workOrder.setCompletionDateTime(null);

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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);

        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_repairWorkOrder_returnsOriginalDevice() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_noMaintenanceRequired_updatesLastPmOnly() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_rollsBackComplianceDate_retainsOriginalCompliance() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_willAdvanceComplianceDate_updatesCompliance() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_nullComplianceDate_updatesCompliance() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2023-04-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/15/2022, so the next pm is due 6/30/2023, but the
        // compliance-through-date is null. we are attempting to update maintenance stats with a pm
        // completed 4/15/2023, which should cause the compliance-through-date to advance, because
        // there is no comparison to be made on whether it would roll back compliance
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(LocalDate.of(2022, 6, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 6, 30));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2024, 4, 30));
        copyDevice.setNextPmDueDate(LocalDate.of(2023, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2023, 4, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_notScheduled_syncsNextPmWithCompliance() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_nextCyclePmWithinCompliance_updatesNextPm() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
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
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_nextCyclePmPostCompliance_retainsOriginalNextPm() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2022-06-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 1/31/2022, so the compliance-through-date and next pm due
        // is 1/31/2023. we are attempting to update maintenance stats with a pm completed 6/15/2022,
        // which causes the compliance-through-date to advance to 6/30/2023, but the next pm should not
        // advance to the next normal every-January cycle (1/31/2024) because that would cause it to be
        // due late
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 1, 31));
        device.setLastPmCompletionDate(LocalDate.of(2022, 1, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 1, 31));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2023, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2022, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_rollsBackLastPm_retainsOriginalLastPm() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2022-04-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with a last pm completed 6/30/2023, so the compliance-through-date and next pm due
        // is 6/30/2024. we are attempting to update maintenance stats with a pm completed 4/15/2022,
        // which would cause the last pm completed date to roll back, so we expect it should retain the
        // original last pm completed date of 6/30/2023
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 6, 30));
        device.setLastPmCompletionDate(LocalDate.of(2023, 6, 12));
        device.setNextPmDueDate(LocalDate.of(2024, 6, 30));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    @Test
    public void advanceMaintenanceStatsWithWorkOrderIfApplicable_noLastPm_updatesLastPm() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrder.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrder.setWorkOrderAwaitStatus(WorkOrderAwaitStatus.AWAITING_PARTS);
        workOrder.setCompletionDateTime(new LocalDateTimeConverter()
                .unconvert("2022-06-15T10:00:01"));
        workOrder.setClosedById(workOrder.getCreatedById());
        workOrder.setClosedByName(workOrder.getCreatedByName());
        workOrder.setClosedDateTime(workOrder.getCreationDateTime().plusHours(1));

        // a device with no last pm completed date, but the compliance-through-date and next pm due
        // are 6/30/2024. we are attempting to update maintenance stats with a pm completed 4/15/2022,
        // which should cause the last pm completed date to be set to the same, since there is no comparison
        // to be made
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setComplianceThroughDate(LocalDate.of(2023, 1, 31));
        device.setLastPmCompletionDate(LocalDate.of(2022, 1, 12));
        device.setNextPmDueDate(LocalDate.of(2023, 1, 31));

        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        Device copyDevice = copyDevice(device);
        copyDevice.setComplianceThroughDate(LocalDate.of(2023, 6, 30));
        copyDevice.setLastPmCompletionDate(LocalDate.of(2022, 6, 15));

        // WHEN
        Device result = closeWorkOrderActivity.advanceMaintenanceStatsWithWorkOrderIfApplicable("WR123");

        // THEN
        assertEquals(copyDevice, result, "The device as updated by the close work order activity did not " +
                "match what was expected");
    }

    private WorkOrder copyWorkOrder(WorkOrder workOrderToCopy) {
        WorkOrder copyWorkOrder = new WorkOrder();
        copyWorkOrder.setWorkOrderId(workOrderToCopy.getWorkOrderId());
        copyWorkOrder.setWorkOrderType(workOrderToCopy.getWorkOrderType());
        copyWorkOrder.setControlNumber(workOrderToCopy.getControlNumber());
        copyWorkOrder.setSerialNumber(workOrderToCopy.getSerialNumber());
        copyWorkOrder.setWorkOrderCompletionStatus(workOrderToCopy.getWorkOrderCompletionStatus());
        copyWorkOrder.setWorkOrderAwaitStatus(workOrderToCopy.getWorkOrderAwaitStatus());
        copyWorkOrder.setManufacturerModel(workOrderToCopy.getManufacturerModel());
        copyWorkOrder.setFacilityName(workOrderToCopy.getFacilityName());
        copyWorkOrder.setAssignedDepartment(workOrderToCopy.getAssignedDepartment());
        copyWorkOrder.setProblemReported(workOrderToCopy.getProblemReported());
        copyWorkOrder.setProblemFound(workOrderToCopy.getProblemFound());
        copyWorkOrder.setCreatedById(workOrderToCopy.getCreatedById());
        copyWorkOrder.setCreatedByName(workOrderToCopy.getCreatedByName());
        copyWorkOrder.setCreationDateTime(workOrderToCopy.getCreationDateTime());
        copyWorkOrder.setClosedById(workOrderToCopy.getClosedById());
        copyWorkOrder.setClosedByName(workOrderToCopy.getClosedByName());
        copyWorkOrder.setClosedDateTime(workOrderToCopy.getClosedDateTime());
        copyWorkOrder.setSummary(workOrderToCopy.getSummary());
        copyWorkOrder.setCompletionDateTime(workOrderToCopy.getCompletionDateTime());

        return copyWorkOrder;
    }

    private Device copyDevice(Device deviceToCopy) {
        Device deviceCopy = new Device();
        deviceCopy.setControlNumber(deviceToCopy.getControlNumber());
        deviceCopy.setSerialNumber(deviceToCopy.getSerialNumber());
        ManufacturerModel copyManufacturerModel = new ManufacturerModel();
        copyManufacturerModel.setManufacturer(deviceToCopy.getManufacturerModel().getManufacturer());
        copyManufacturerModel.setModel(deviceToCopy.getManufacturerModel().getModel());
        copyManufacturerModel.setRequiredMaintenanceFrequencyInMonths(deviceToCopy.getManufacturerModel()
                .getRequiredMaintenanceFrequencyInMonths());
        deviceCopy.setManufactureDate(deviceToCopy.getManufactureDate());
        deviceCopy.setManufacturerModel(copyManufacturerModel);
        deviceCopy.setServiceStatus(deviceToCopy.getServiceStatus());
        deviceCopy.setFacilityName(deviceToCopy.getFacilityName());
        deviceCopy.setAssignedDepartment(deviceToCopy.getAssignedDepartment());
        deviceCopy.setComplianceThroughDate(deviceToCopy.getComplianceThroughDate());
        deviceCopy.setLastPmCompletionDate(deviceToCopy.getLastPmCompletionDate());
        deviceCopy.setNextPmDueDate(deviceToCopy.getNextPmDueDate());
        deviceCopy.setInventoryAddDate(deviceToCopy.getInventoryAddDate());
        deviceCopy.setAddedById(deviceToCopy.getAddedById());
        deviceCopy.setAddedByName(deviceToCopy.getAddedByName());
        deviceCopy.setNotes(deviceToCopy.getNotes());

        return deviceCopy;
    }
}
