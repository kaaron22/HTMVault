package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private String controlNumber = "123";
    private String serialNumber = "456";
    private String manufacturer = "a manufacturer";
    private String model = "a model";
    private ManufacturerModel manufacturerModel = new ManufacturerModel();
    private String manufactureDate = "2023-05-26";
    private String facilityName = "a hospital";
    private String assignedDepartment = "ER";
    private FacilityDepartment facilityDepartment = new FacilityDepartment();
    private int maintenanceFrequencyInMonths = 6;
    private String notes = "some notes";
    private String customerId = "227345";
    private String customerName = "John Doe";

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

        // THEN
        assertNotNull(addDeviceResult.getDeviceModel().getControlNumber());

    }
}