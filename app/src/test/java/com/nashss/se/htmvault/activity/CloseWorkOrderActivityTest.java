package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
}