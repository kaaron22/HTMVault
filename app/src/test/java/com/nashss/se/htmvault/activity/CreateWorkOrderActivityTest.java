package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderCompletionDateTimeComparator;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import com.nashss.se.htmvault.utils.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CreateWorkOrderActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private WorkOrderDao workOrderDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private CreateWorkOrderActivity createWorkOrderActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        createWorkOrderActivity = new CreateWorkOrderActivity(deviceDao, workOrderDao, metricsPublisher);
    }

    @Test
    public void handleRequest_noDeviceFoundForControlNumber_throwsDeviceNotFoundException() {
        // GIVEN
        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .build();
        when(deviceDao.getDevice(anyString())).thenThrow(DeviceNotFoundException.class);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                createWorkOrderActivity.handleRequest(createWorkOrderRequest),
                "Expected a create work order request for a device not found to result in an" +
                        "DeviceNotFoundException thrown");
        verifyNoInteractions(workOrderDao);
    }

    @Test
    public void handleRequest_invalidWorkOrderType_throwsInvalidAttributeValueException() {
        // GIVEN
        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .withWorkOrderType("invalid work order type")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(new Device());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                createWorkOrderActivity.handleRequest(createWorkOrderRequest),
                "Expected a create work order request with an invalid work order type to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
        verifyNoInteractions(workOrderDao);
    }

    @Test
    public void handleRequest_nullProblemReported_throwsInvalidAttributeValueException() {
        // GIVEN
        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .withWorkOrderType("REPAIR")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(new Device());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        createWorkOrderActivity.handleRequest(createWorkOrderRequest),
                "Expected a create work order request with problem reported null to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
        verifyNoInteractions(workOrderDao);
    }

    @Test
    public void handleRequest_blankProblemReported_throwsInvalidAttributeValueException() {
        // GIVEN
        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .withWorkOrderType("REPAIR")
                .withProblemReported("   ")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(new Device());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        createWorkOrderActivity.handleRequest(createWorkOrderRequest),
                "Expected a create work order request with a blank problem reported to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
        verifyNoInteractions(workOrderDao);
    }

    @Test
    public void handleRequest_invalidSortOrder_throwsInvalidAttributeValueException() {
        // GIVEN
        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .withWorkOrderType("REPAIR")
                .withProblemReported("a valid reported problem")
                .withSortOrder("invalid sort order")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(new Device());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        createWorkOrderActivity.handleRequest(createWorkOrderRequest),
                "Expected a create work order request with an invalid sort order to result in an " +
                        "InvalidAttributeValueException thrown");
        verifyNoInteractions(workOrderDao);
        verify(metricsPublisher).addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_validRequestDescending_returnsListWorkOrdersInResultProperlySorted() {
        // GIVEN
        // a mock device to return
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(24);
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setControlNumber("123");
        device.setSerialNumber("SN321");

        // a mock list of four existing work orders for the device to return
        List<WorkOrder> workOrders = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            workOrders.add(WorkOrderTestHelper.generateWorkOrder(i, "123", "SN321",
                    manufacturerModel, "TestFacility", "TestDepartment"));
        }

        // set all except the most recent existing one as closed, and with completion dates/times
        for (WorkOrder workOrder : workOrders) {
            workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        }

        // this one will be a recently opened mock work order that has yet to be completed/closed (not yet sorted)
        workOrders.get(1).setCreationDateTime(LocalDateTime.of(LocalDate.of(2023, 6, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(1).setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrders.get(1).setCompletionDateTime(null);

        // the creation and completion dates/times for the older mocked work orders (not yet sorted)
        workOrders.get(0).setCreationDateTime(LocalDateTime.of(LocalDate.of(2020, 5, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(0).setCompletionDateTime(LocalDateTime.of(LocalDate.of(2020, 5, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(2).setCreationDateTime(LocalDateTime.of(LocalDate.of(2021, 5, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(2).setCompletionDateTime(LocalDateTime.of(LocalDate.of(2021, 5, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(3).setCreationDateTime(LocalDateTime.of(LocalDate.of(2022, 5, 10),
                LocalTime.of(17, 10, 0)));
        workOrders.get(3).setCompletionDateTime(LocalDateTime.of(LocalDate.of(2022, 5, 10),
                LocalTime.of(17, 10, 0)));

        CreateWorkOrderRequest createWorkOrderRequest = CreateWorkOrderRequest.builder()
                .withControlNumber("123")
                .withWorkOrderType("ACCEPTANCE_TESTING")
                .withProblemReported("a valid reported problem")
                .withSortOrder("DESCENDING")
                .build();

        when(deviceDao.getDevice(anyString())).thenReturn(device);
        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        // the list of work order IDs in the order we expect to see them after being sorted
        List<String> expectedOrderWorkOrders = new ArrayList<>(Arrays.asList(workOrders.get(1).getWorkOrderId(),
                workOrders.get(3).getWorkOrderId(), workOrders.get(2).getWorkOrderId(),
                workOrders.get(0).getWorkOrderId()));

        CreateWorkOrderResult createWorkOrderResult = createWorkOrderActivity.handleRequest(createWorkOrderRequest);
        List<WorkOrderModel> workOrderModels = createWorkOrderResult.getWorkOrders();

        // add the new work order id created by the request to the beginning of the list, as we expect to see
        expectedOrderWorkOrders.add(0, workOrderModels.get(0).getWorkOrderId());

        // THEN
        // verify the work orders are properly converted to work order models
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        // verify the work order models are in the proper sort order
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);
        verify(workOrderDao).saveWorkOrder(any(WorkOrder.class));
        verify(metricsPublisher).addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    private void assertWorkOrderModelsSortedCorrectly(List<String> expectedSortedWorkOrderIds,
                                                      List<WorkOrderModel> sortedWorkOrderModels) {
        for(int i = 0; i < sortedWorkOrderModels.size(); i++) {
            assertEquals(expectedSortedWorkOrderIds.get(i), sortedWorkOrderModels.get(i).getWorkOrderId());
        }
    }
}