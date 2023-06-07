package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetDeviceWorkOrdersActivityTest {

    @Mock
    WorkOrderDao workOrderDao;
    @Mock
    MetricsPublisher metricsPublisher;
    @InjectMocks
    GetDeviceWorkOrdersActivity getDeviceWorkOrdersActivity;

    private LocalDateTime basis = LocalDateTime.now();
    private List<WorkOrder> workOrders;
    private ManufacturerModel manufacturerModel;


    @BeforeEach
    void setUp() {
        openMocks(this);

        basis = LocalDateTime.now();
        manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        workOrders = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            workOrders.add(WorkOrderTestHelper.generateWorkOrder(i, "123", "SN123",
                    manufacturerModel, "TestFacility", "TestDepartment"));
        }
    }

    @Test
    public void handleRequest_descendingSortAllCompleted_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        // a request with a descending sort specified
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("DESCENDING")
                .build();

        // set work orders unsorted by completion DateTime
        workOrders.get(0).setCompletionDateTime(basis.minusDays(3));
        workOrders.get(1).setCompletionDateTime(basis.minusDays(2));
        workOrders.get(2).setCompletionDateTime(basis.minusDays(7));
        workOrders.get(3).setCompletionDateTime(basis.minusDays(5));

        // the work orders ids sorted in the order we expect to see after the activity runs (descending by DateTime)
        List<String> expectedOrderWorkOrders = new ArrayList<>(Arrays.asList(workOrders.get(1).getWorkOrderId(),
                workOrders.get(0).getWorkOrderId(), workOrders.get(3).getWorkOrderId(),
                workOrders.get(2).getWorkOrderId()));

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        // converted correctly from WorkOrders to WorkOrderModels
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        // verify they were sorted as expected
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);

        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_ascendingSort_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        // a request with an ascending sort specified
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("ASCENDING")
                .build();

        // set work orders unsorted by completion DateTime
        workOrders.get(0).setCompletionDateTime(basis.minusDays(3));
        workOrders.get(1).setCompletionDateTime(basis.minusDays(2));
        workOrders.get(2).setCompletionDateTime(basis.minusDays(7));
        workOrders.get(3).setCompletionDateTime(basis.minusDays(5));

        // the work orders ids sorted in the order we expect to see after the activity runs (ascending by DateTime)
        List<String> expectedOrderWorkOrders = new ArrayList<>(Arrays.asList(workOrders.get(2).getWorkOrderId(),
                workOrders.get(3).getWorkOrderId(), workOrders.get(0).getWorkOrderId(),
                workOrders.get(1).getWorkOrderId()));

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        // converted correctly from WorkOrders to WorkOrderModels
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        // verify they were sorted as expected
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);

        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_noSortSpecified_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        // a request with no sort specified
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .build();
        // set work orders unsorted by completion DateTime
        workOrders.get(0).setCompletionDateTime(basis.minusDays(3));
        workOrders.get(1).setCompletionDateTime(basis.minusDays(2));
        workOrders.get(2).setCompletionDateTime(basis.minusDays(7));
        workOrders.get(3).setCompletionDateTime(basis.minusDays(5));

        // the work orders ids sorted in the order we expect to see after the activity runs (descending by DateTime)
        List<String> expectedOrderWorkOrders = new ArrayList<>(Arrays.asList(workOrders.get(1).getWorkOrderId(),
                workOrders.get(0).getWorkOrderId(), workOrders.get(3).getWorkOrderId(),
                workOrders.get(2).getWorkOrderId()));

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        // converted correctly from WorkOrders to WorkOrderModels
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        // verify they were sorted as expected
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);
        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_oneNullCompletionDateTime_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("DESCENDING")
                .build();

        // set work orders unsorted by completion DateTime, with one completionDateTime null (it should be first in
        // the sorted list)
        workOrders.get(0).setCompletionDateTime(basis.minusDays(3));
        workOrders.get(1).setCompletionDateTime(null);
        workOrders.get(2).setCompletionDateTime(basis.minusDays(7));
        workOrders.get(3).setCompletionDateTime(basis.minusDays(5));

        // the work orders ids sorted in the order we expect to see after the activity runs (descending by DateTime)
        List<String> expectedOrderWorkOrders = new ArrayList<>(Arrays.asList(workOrders.get(1).getWorkOrderId(),
                workOrders.get(0).getWorkOrderId(), workOrders.get(3).getWorkOrderId(),
                workOrders.get(2).getWorkOrderId()));

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);
        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_nullCompletionDateTimes_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("DESCENDING")
                .build();

        // since these completion date times are null, the comparator in our activity should then sort them by the
        // work order id, which we will set to be unsorted
        workOrders.get(0).setCompletionDateTime(null);
        workOrders.get(1).setCompletionDateTime(null);
        workOrders.get(2).setCompletionDateTime(null);
        workOrders.get(3).setCompletionDateTime(null);
        workOrders.get(0).setWorkOrderId("WR022");
        workOrders.get(1).setWorkOrderId("WR731");
        workOrders.get(2).setWorkOrderId("WR244");
        workOrders.get(3).setWorkOrderId("WR304");

        // obtain the work order ids from the list of work orders and sort them in the order we expect to see
        // when we inspect the work order models returned
        List<String> expectedOrderWorkOrders = sortWorkOrderIds(workOrders);
        Collections.reverse(expectedOrderWorkOrders);

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);
        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_equalCompletionDateTimes_returnsWorkOrderModelListInResultSortedCorrectly() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("DESCENDING")
                .build();

        // since these completion date times are matching, the comparator in our activity should sort them by the
        // work order id, which we will set to be unsorted
        workOrders.get(0).setCompletionDateTime(basis);
        workOrders.get(1).setCompletionDateTime(basis);
        workOrders.get(2).setCompletionDateTime(basis);
        workOrders.get(3).setCompletionDateTime(basis);
        workOrders.get(0).setWorkOrderId("WR022");
        workOrders.get(1).setWorkOrderId("WR731");
        workOrders.get(2).setWorkOrderId("WR244");
        workOrders.get(3).setWorkOrderId("WR304");

        // obtain the work order ids from the list of work orders and sort them in the order we expect to see
        // when we inspect the work order models returned
        List<String> expectedOrderWorkOrders = sortWorkOrderIds(workOrders);
        Collections.reverse(expectedOrderWorkOrders);
        System.out.println(expectedOrderWorkOrders);

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        assertWorkOrderModelsSortedCorrectly(expectedOrderWorkOrders, workOrderModels);
        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    @Test
    public void handleRequest_requestInvalidSortOrder_throwsInvalidAttributeValueException() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .withSortOrder("INVALID")
                .build();

        List<WorkOrder> workOrders = new ArrayList<>();
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");

        for (int i = 0; i < 4; i++) {
            workOrders.add(WorkOrderTestHelper.generateWorkOrder(i, "123", "SN123",
                    manufacturerModel, "TestFacility", "TestDepartment"));
        }

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest),
                "Expected invalid sort order in request to result in InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_noWorkOrdersFoundForControlNumber_returnsEmptyListWorkOrderModelsInResult() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .build();

        List<WorkOrder> workOrders = new ArrayList<>();
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // THEN
        assertTrue(workOrderModels.isEmpty());
        verify(workOrderDao).getWorkOrders("123");
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }

    private List<String> sortWorkOrderIds(List<WorkOrder> generatedWorkOrders) {
        List<String> sortedWorkOrderIds = new ArrayList<>();
        for (WorkOrder workOrder : generatedWorkOrders) {
            sortedWorkOrderIds.add(workOrder.getWorkOrderId());
        }
        Collections.sort(sortedWorkOrderIds);
        return sortedWorkOrderIds;
    }

    private void assertWorkOrderModelsSortedCorrectly(List<String> expectedSortedWorkOrderIds,
                                                       List<WorkOrderModel> sortedWorkOrderModels) {
        for(int i = 0; i < sortedWorkOrderModels.size(); i++) {
            assertEquals(expectedSortedWorkOrderIds.get(i), sortedWorkOrderModels.get(i).getWorkOrderId());
        }
    }

}