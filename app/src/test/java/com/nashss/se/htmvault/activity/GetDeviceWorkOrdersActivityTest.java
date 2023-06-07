package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetDeviceWorkOrdersActivityTest {

    @Mock
    WorkOrderDao workOrderDao;
    @Mock
    MetricsPublisher metricsPublisher;
    @InjectMocks
    GetDeviceWorkOrdersActivity getDeviceWorkOrdersActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void handleRequest_validRequestNoSortSpecified_returnsWorkOrderModelListInResultSortedByWorkOrderId() {
        // GIVEN
        GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest = GetDeviceWorkOrdersRequest.builder()
                .withControlNumber("123")
                .build();

        List<WorkOrder> workOrders = new ArrayList<>();
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");

        for (int i = 0; i < 4; i++) {
            workOrders.add(WorkOrderTestHelper.generateWorkOrder(i, "123", "SN123",
                    manufacturerModel, "TestFacility", "TestDepartment"));
        }

        when(workOrderDao.getWorkOrders(anyString())).thenReturn(workOrders);

        // WHEN
        GetDeviceWorkOrdersResult getDeviceWorkOrdersResult =
                getDeviceWorkOrdersActivity.handleRequest(getDeviceWorkOrdersRequest);
        List<WorkOrderModel> workOrderModels = getDeviceWorkOrdersResult.getWorkOrders();

        // list of workOrderIds assigned to generated work orders
        List<String> expectedSortedWorkOrderIds = sortedWorkOrderIds(workOrders);

        // expected descending sort order with no sort order set in request
        Collections.reverse(expectedSortedWorkOrderIds);

        // THEN
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
        assertWorkOrderModelsSortedCorrectly(expectedSortedWorkOrderIds, workOrderModels);
    }

    private List<String> sortedWorkOrderIds(List<WorkOrder> generatedWorkOrders) {
        List<String> workOrderIds = new ArrayList<>();
        for (WorkOrder workOrder : generatedWorkOrders) {
            workOrderIds.add(workOrder.getWorkOrderId());
        }
        Collections.sort(workOrderIds);
        return workOrderIds;
    }

    private void assertWorkOrderModelsSortedCorrectly(List<String> expectedSortedWorkOrderIds,
                                                       List<WorkOrderModel> sortedWorkOrderModels) {
        for(int i = 0; i < sortedWorkOrderModels.size(); i++) {
            assertEquals(expectedSortedWorkOrderIds.get(i), sortedWorkOrderModels.get(i).getWorkOrderId());
        }
    }

}