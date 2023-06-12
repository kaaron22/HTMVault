package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
    }


}