package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.GetWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetWorkOrderActivityTest {
    @Mock
    private WorkOrderDao workOrderDao;
    private MetricsPublisher metricsPublisher;

    private GetWorkOrderActivity getWorkOrderActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        getWorkOrderActivity = new GetWorkOrderActivity(workOrderDao, metricsPublisher);
    }

    @Test
    public void handleRequest_workOrderFound_returnsWorkOrderModelInResult() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(0);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");

        GetWorkOrderRequest getWorkOrderRequest = GetWorkOrderRequest.builder()
                        .withWorkOrderId(workOrder.getWorkOrderId())
                        .build();

        when(workOrderDao.getWorkOrder(workOrder.getWorkOrderId())).thenReturn(workOrder);

        // WHEN
        GetWorkOrderResult getWorkOrderResult = getWorkOrderActivity.handleRequest(getWorkOrderRequest);

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(workOrder, getWorkOrderResult.getWorkOrder());
    }
}
