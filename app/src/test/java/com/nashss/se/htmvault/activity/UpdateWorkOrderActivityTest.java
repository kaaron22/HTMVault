package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    }


}