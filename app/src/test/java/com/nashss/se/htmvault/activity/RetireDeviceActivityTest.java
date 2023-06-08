package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.RetireDeviceWithOpenWorkOrdersException;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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

        when(deviceDao.getDevice(Mockito.anyString())).thenThrow(DeviceNotFoundException.class);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                retireDeviceActivity.handleRequest(retireDeviceRequest),
                "Expected request with control number not found to result in DeviceNotFoundException thrown");
    }

    @Test
    public void handleRequest_deviceHasAnOpenWorkOrder_throwsRetireDeviceWithOpenWorkOrdersException() {
        // GIVEN
        // a mock device to return when our method under test mocks the call to look for it by control number
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        RetireDeviceRequest retireDeviceRequest = RetireDeviceRequest.builder()
                .withControlNumber(device.getControlNumber())
                .withCustomerId("an ID")
                .withCustomerName("a name")
                .build();
        when(deviceDao.getDevice("123")).thenReturn(device);
        when(workOrderDao.getWorkOrders(anyString())).thenThrow(RetireDeviceWithOpenWorkOrdersException.class);

        // WHEN & THEN
        assertThrows(RetireDeviceWithOpenWorkOrdersException.class, () ->
                retireDeviceActivity.handleRequest(retireDeviceRequest),
                "Expected request to retire a device with open work order(s) to result in " +
                        "RetireDeviceWithOpenWorkOrdersException to be thrown");
    }
}