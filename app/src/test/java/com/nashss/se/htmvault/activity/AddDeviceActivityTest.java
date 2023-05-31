package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AddDeviceActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private ManufacturerModelDao manufacturerModelDao;
    @Mock
    private FacilityDepartmentDao facilityDepartmentDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private AddDeviceActivity addDeviceActivity;

    Device device = new Device();
    private final String controlNumber = "123";
    private final String serialNumber = "456";
    private final String manufacturer = "a manufacturer";
    private final String model = "a model";
    private final ManufacturerModel manufacturerModel = new ManufacturerModel();
    private final String manufactureDate = "2023-05-26";
    private final String facilityName = "a hospital";
    private final String assignedDepartment = "ER";
    private final FacilityDepartment facilityDepartment = new FacilityDepartment();
    private final int maintenanceFrequencyInMonths = 6;
    private final String notes = "some notes";
    private final String customerId = "227345";
    private final String customerName = "John Doe";

    @BeforeEach
    void setUp() {
        openMocks(this);
        addDeviceActivity = new AddDeviceActivity(deviceDao, manufacturerModelDao, facilityDepartmentDao,
                metricsPublisher);
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        facilityDepartment.setFacilityName(facilityName);
        facilityDepartment.setAssignedDepartment(assignedDepartment);
        device.setControlNumber(controlNumber);
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(LocalDate.now());
        device.setMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById("1234");
        device.setAddedByName("John Doe");
        device.setNotes(notes);
        device.setWorkOrders(new ArrayList<>());
    }

    @Test
    public void handleRequest_withAllValuesAcceptable_createsAndSavesDevice() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN
        AddDeviceResult addDeviceResult = addDeviceActivity.handleRequest(addDeviceRequest);
        DeviceModel deviceModel = addDeviceResult.getDeviceModel();

        // THEN
        verify(deviceDao).saveDevice(any(Device.class));
        assertNotNull(deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals(manufactureDate, deviceModel.getManufactureDate());
        assertEquals(ServiceStatus.IN_SERVICE.toString(), deviceModel.getServiceStatus());
        assertEquals(facilityName, deviceModel.getFacilityName());
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment());
        assertEquals("", deviceModel.getComplianceThroughDate());
        assertEquals("", deviceModel.getLastPmCompletionDate());
        assertEquals();

    }

    @Test
    public void handleRequest_withRequiredValueNull_throwsInvalidAttributeValueException() {
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(null)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a null value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
    }

    @Test
    public void handleRequest_withOptionalValueNull_createsAndSavesDevice() {
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(null)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN
        AddDeviceResult addDeviceResult = addDeviceActivity.handleRequest(addDeviceRequest);

        // THEN

        assertDoesNotThrow(() -> addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a null value for an optional attribute to result in an " +
                        "InvalidAttributeValueException thrown");
    }


}
