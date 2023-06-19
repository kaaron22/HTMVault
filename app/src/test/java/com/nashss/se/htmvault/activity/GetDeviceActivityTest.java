package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetDeviceActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private GetDeviceActivity getDeviceActivity;

    private final Device device = new Device();
    private final String controlNumber = "123";
    private final String serialNumber = "G-456";
    private final String manufacturer = "a manufacturer";
    private final String model = "a model";
    private final ManufacturerModel manufacturerModel = new ManufacturerModel();
    private final String manufactureDate = "2023-05-26";
    private final String facilityName = "a hospital";
    private final String assignedDepartment = "ER";
    private final Integer maintenanceFrequencyInMonths = 6;
    private final String notes = "some notes";
    private final String customerId = "227345";
    private final String customerName = "John Doe";

    @BeforeEach
    void setUp() {
        openMocks(this);
        getDeviceActivity = new GetDeviceActivity(deviceDao, metricsPublisher);

        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
        device.setControlNumber(controlNumber);
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(new LocalDateConverter().unconvert(manufactureDate));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(LocalDate.now());
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById(customerId);
        device.setAddedByName(customerName);
        device.setNotes(notes);
    }

    @Test
    public void handleRequest_deviceFound_returnsDeviceModelInResult() {
        // GIVEN
        GetDeviceRequest getDeviceRequest = GetDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .build();
        when(deviceDao.getDevice(anyString())).thenReturn(device);

        // WHEN
        GetDeviceResult getDeviceResult = getDeviceActivity.handleRequest(getDeviceRequest);
        DeviceModel deviceModel = getDeviceResult.getDevice();

        // THEN
        verify(deviceDao).getDevice(anyString());
        assertEquals(controlNumber, deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals(manufactureDate, deviceModel.getManufactureDate());
        assertEquals(ServiceStatus.IN_SERVICE.toString(), deviceModel.getServiceStatus());
        assertEquals(facilityName, deviceModel.getFacilityName());
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment());
        assertEquals("", deviceModel.getComplianceThroughDate());
        assertEquals("", deviceModel.getLastPmCompletionDate());
        assertEquals(LocalDate.now().toString(), deviceModel.getNextPmDueDate());
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths());
        assertEquals(LocalDate.now().toString(), deviceModel.getInventoryAddDate());
        assertEquals(customerId, deviceModel.getAddedById());
        assertEquals(customerName, deviceModel.getAddedByName());
        assertEquals(notes, deviceModel.getNotes());
    }
}
