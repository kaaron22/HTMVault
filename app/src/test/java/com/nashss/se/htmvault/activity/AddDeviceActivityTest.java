package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
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
    @Mock
    private DynamoDBMapper dynamoDBMapper;

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
        device.setManufactureDate(new LocalDateConverter().unconvert(manufactureDate));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(LocalDate.now());
        device.setMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById(customerId);
        device.setAddedByName(customerName);
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
        assertTrue(deviceModel.getWorkOrderSummaries().isEmpty());
    }

    @Test
    public void handleRequest_withRequiredValueNull_throwsInvalidAttributeValueException() {
        // GIVEN
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

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a null value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
    }

    @Test
    public void handleRequest_withOptionalValueNull_createsAndSavesDevice() {
        // GIVEN
        device.setManufactureDate(null);
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
        DeviceModel deviceModel = addDeviceResult.getDeviceModel();

        // THEN
        verify(deviceDao).saveDevice(any(Device.class));
        assertEquals(controlNumber, deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals("", deviceModel.getManufactureDate());
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
        assertTrue(deviceModel.getWorkOrderSummaries().isEmpty());
    }

    @Test
    public void handleRequest_withRequiredValueEmpty_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber("")
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

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected an empty value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
    }

    @Test
    public void handleRequest_withRequiredValueBlank_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber("   ")
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

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a blank value for serial number to result in an InvalidAttributeValueException " +
                        "thrown");
    }

    @Test
    public void handleRequest_withControlNumberContainsANonAlphaNumericChar_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber("1234-")
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

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a control number containing an invalid character to result in an " +
                        "InvalidAttributeValueException thrown");
    }

    @Test
    public void handleRequest_withSerialNumberContainsAnInvalidCharacter_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber("1234-+")
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

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a serial number containing an invalid character to result in an " +
                        "InvalidAttributeValueException thrown");
    }

    @Test
    public void handleRequest_withManufacturerModelDoesNotExist_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer("not a real manufacturer")
                .withModel("not a real model")
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        doThrow(ManufacturerModelNotFoundException.class)
                .when(manufacturerModelDao).getManufacturerModel(anyString(), anyString());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a manufacturer/model not found to result in an InvalidAttributeValueException " +
                        "thrown");
    }
}
