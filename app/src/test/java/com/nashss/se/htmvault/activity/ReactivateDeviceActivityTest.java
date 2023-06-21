package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.ReactivateDeviceRequest;
import com.nashss.se.htmvault.activity.results.ReactivateDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ReactivateDeviceActivityTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    private DeviceDao deviceDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private ReactivateDeviceActivity reactivateDeviceActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        deviceDao = new DeviceDao(dynamoDBMapper, metricsPublisher);
        reactivateDeviceActivity = new ReactivateDeviceActivity(deviceDao, metricsPublisher);
    }

    @Test
    public void handleRequest_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        ReactivateDeviceRequest reactivateDeviceRequest = ReactivateDeviceRequest.builder()
                .withControlNumber("123")
                .withCustomerId("an ID")
                .withCustomerName("a name")
                .build();

        when(dynamoDBMapper.load(eq(Device.class), Mockito.anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                        reactivateDeviceActivity.handleRequest(reactivateDeviceRequest),
                "Expected request with control number not found to result in DeviceNotFoundException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.REACTIVATEDEVICE_DEVICENOTFOUND_COUNT, 1);
    }

    @Test
    public void handleRequest_requestDeviceReactivation_updatesDeviceToInServiceAndReturnsInResult() {
        // GIVEN
        // a mock retired device to return when our method under test mocks the call to look for it by control number
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");
        device.setServiceStatus(ServiceStatus.RETIRED);

        // our final expected device (used to ensure the only thing that ultimately changed was the service status)
        Device expectedDevice = copyDevice(device);
        expectedDevice.setServiceStatus(ServiceStatus.IN_SERVICE);

        ReactivateDeviceRequest reactivateDeviceRequest = ReactivateDeviceRequest.builder()
                .withControlNumber(device.getControlNumber())
                .withCustomerId("an ID")
                .withCustomerName("a name")
                .build();
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);

        // WHEN
        ReactivateDeviceResult result = reactivateDeviceActivity.handleRequest(reactivateDeviceRequest);

        // THEN
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
        verify(metricsPublisher).addCount(MetricsConstants.REACTIVATEDEVICE_DEVICENOTFOUND_COUNT, 0);
        assertEquals("IN_SERVICE", result.getDevice().getServiceStatus());

        // verify no other device information was modified by our method under test
        DeviceTestHelper.assertDeviceEqualsDeviceModel(expectedDevice, result.getDevice());
    }

    private Device copyDevice(Device device) {
        Device deviceCopy = new Device();
        deviceCopy.setControlNumber(device.getControlNumber());
        deviceCopy.setSerialNumber(device.getSerialNumber());
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(device.getManufacturerModel().getManufacturer());
        manufacturerModel.setModel(device.getManufacturerModel().getModel());
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(device.getManufacturerModel()
                .getRequiredMaintenanceFrequencyInMonths());
        deviceCopy.setManufactureDate(device.getManufactureDate());
        deviceCopy.setManufacturerModel(manufacturerModel);
        deviceCopy.setServiceStatus(device.getServiceStatus());
        deviceCopy.setFacilityName(device.getFacilityName());
        deviceCopy.setAssignedDepartment(device.getAssignedDepartment());
        deviceCopy.setComplianceThroughDate(device.getComplianceThroughDate());
        deviceCopy.setLastPmCompletionDate(device.getLastPmCompletionDate());
        deviceCopy.setNextPmDueDate(device.getNextPmDueDate());
        deviceCopy.setInventoryAddDate(device.getInventoryAddDate());
        deviceCopy.setAddedById(device.getAddedById());
        deviceCopy.setAddedByName(device.getAddedByName());
        deviceCopy.setNotes(device.getNotes());

        return deviceCopy;
    }
}
