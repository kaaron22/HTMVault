package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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


}