package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RetireDeviceActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private WorkOrderDao workOrderDao;

    @InjectMocks
    private RetireDeviceActivity retireDeviceActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void handleRequest_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        RetireDeviceRequest retireDeviceRequest = RetireDeviceRequest.builder()
                        .withControlNumber("123")
                        .withCustomerId("an ID")
                        .withCustomerName("a name")
                        .build();

        Mockito.when(deviceDao.getDevice(Mockito.anyString())).thenThrow(DeviceNotFoundException.class);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                retireDeviceActivity.handleRequest(retireDeviceRequest),
                "Expected request with control number not found to result in DeviceNotFoundException thrown");
    }
}