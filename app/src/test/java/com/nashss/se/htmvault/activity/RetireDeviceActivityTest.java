package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.RetireDeviceWithOpenWorkOrdersException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class RetireDeviceActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private WorkOrderDao workOrderDao;
    @Mock
    private MetricsPublisher metricsPublisher;

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

        when(deviceDao.getDevice(anyString())).thenThrow(DeviceNotFoundException.class);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                retireDeviceActivity.handleRequest(retireDeviceRequest),
                "Expected request with control number not found to result in DeviceNotFoundException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 1);
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

        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestManufacturer", "TestModel");
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        when(deviceDao.getDevice(anyString())).thenReturn(device);
        when(workOrderDao.getWorkOrders(anyString())).thenReturn(new ArrayList<>(List.of(workOrder)));

        // WHEN & THEN
        assertThrows(RetireDeviceWithOpenWorkOrdersException.class, () ->
                retireDeviceActivity.handleRequest(retireDeviceRequest),
                "Expected request to retire a device with open work order(s) to result in " +
                        "RetireDeviceWithOpenWorkOrdersException to be thrown");
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 0);
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_WORKORDERSOPEN_COUNT, 1);
    }

    @Test
    public void handleRequest_deviceEligibleToBeRetired_softDeletesDeviceAndReturnsInResult() {
        // GIVEN
        // a mock device to return when our method under test mocks the call to look for it by control number
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        // our final expected device (used to ensure the only thing that ultimately changed was the service status)
        Device expectedDevice = copyDevice(device);
        expectedDevice.setServiceStatus(ServiceStatus.RETIRED);

        RetireDeviceRequest retireDeviceRequest = RetireDeviceRequest.builder()
                .withControlNumber(device.getControlNumber())
                .withCustomerId("an ID")
                .withCustomerName("a name")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(device);

        // a list of all the work orders for the device with none open
        List<WorkOrder> workOrders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(i, device.getControlNumber(),
                    "SN1", manufacturerModel, "TestFacility",
                    "TestDepartment");
            // our generator randomly sets the completion status, so we need ensure it's set to closed
            workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);
            workOrders.add(workOrder);
        }
        when(workOrderDao.getWorkOrders("123")).thenReturn(workOrders);

        // WHEN
        RetireDeviceResult result = retireDeviceActivity.handleRequest(retireDeviceRequest);

        // THEN
        verify(deviceDao).saveDevice(device);
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 0);
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_WORKORDERSOPEN_COUNT, 0);
        assertEquals("RETIRED", result.getDevice().getServiceStatus());

        // verify no other device information was modified by our method under test
        DeviceTestHelper.assertDeviceEqualsDeviceModel(expectedDevice, result.getDevice());
    }

    @Test
    public void handleRequest_noWorkOrdersForDevice_softDeletesDeviceAndReturnsInResult() {
        // GIVEN
        // a mock device to return when our method under test mocks the call to look for it by control number
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        Device device = DeviceTestHelper.generateActiveDevice(1, manufacturerModel,
                "TestFacility", "TestDepartment");

        // our final expected device (used to ensure the only thing that ultimately changed was the service status)
        Device expectedDevice = copyDevice(device);
        expectedDevice.setServiceStatus(ServiceStatus.RETIRED);

        RetireDeviceRequest retireDeviceRequest = RetireDeviceRequest.builder()
                .withControlNumber(device.getControlNumber())
                .withCustomerId("an ID")
                .withCustomerName("a name")
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(device);

        // the device has no work orders
        when(workOrderDao.getWorkOrders("123")).thenReturn(new ArrayList<>());

        // WHEN
        RetireDeviceResult result = retireDeviceActivity.handleRequest(retireDeviceRequest);

        // THEN
        verify(deviceDao).saveDevice(device);
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 0);
        verify(metricsPublisher).addCount(MetricsConstants.RETIREDEVICE_WORKORDERSOPEN_COUNT, 0);
        assertEquals("RETIRED", result.getDevice().getServiceStatus());

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
