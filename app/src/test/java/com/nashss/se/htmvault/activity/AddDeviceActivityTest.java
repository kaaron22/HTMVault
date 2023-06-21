package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DevicePreviouslyAddedException;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
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
    @Mock
    private DynamoDBMapper dynamoDBMapper;

    private AddDeviceActivity addDeviceActivity;

    private final Device device = new Device();
    private final String controlNumber = "123";
    private final String serialNumber = "G-456";
    private final String manufacturer = "a manufacturer";
    private final String model = "a model";
    private final ManufacturerModel manufacturerModel = new ManufacturerModel();
    private final String manufactureDate = "2023-05-26";
    private final String facilityName = "a hospital";
    private final String assignedDepartment = "ER";
    private final FacilityDepartment facilityDepartment = new FacilityDepartment();
    private final Integer maintenanceFrequencyInMonths = 6;
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
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
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
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById(customerId);
        device.setAddedByName(customerName);
        device.setNotes(notes);
    }

    @Test
    public void handleRequest_withAllValuesAcceptable_createsAndSavesDevice() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN
        AddDeviceResult addDeviceResult = addDeviceActivity.handleRequest(addDeviceRequest);
        DeviceModel deviceModel = addDeviceResult.getDevice();

        // THEN
        verify(deviceDao).saveDevice(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        assertEquals(controlNumber, deviceModel.getControlNumber(), "expected the control number to match");
        assertEquals(serialNumber, deviceModel.getSerialNumber(), "expected the serial number to match");
        assertEquals(manufacturer, deviceModel.getManufacturer(), "expected the manufacturer to match");
        assertEquals(model, deviceModel.getModel(), "expected the model to match");
        assertEquals(manufactureDate, deviceModel.getManufactureDate(), "expected the manufacture date to match");
        assertEquals(ServiceStatus.IN_SERVICE.toString(), deviceModel.getServiceStatus(), "expected the " +
                "service status for the device to match");
        assertEquals(facilityName, deviceModel.getFacilityName(), "expected the facility name to match");
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment(), "expected the department to " +
                "match");
        assertEquals("", deviceModel.getComplianceThroughDate(), "expected the " +
                "compliance-through-date to match");
        assertEquals("", deviceModel.getLastPmCompletionDate(), "expected the last pm completion " +
                "date to match");
        assertEquals(LocalDate.now().toString(), deviceModel.getNextPmDueDate(), "expected the next pm due " +
                "date to match");
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths(), "expected " +
                "the maintenance frequency to match");
        assertEquals(LocalDate.now().toString(), deviceModel.getInventoryAddDate(), "expected the inventory " +
                "add date to match");
        assertEquals(customerId, deviceModel.getAddedById(), "expected the id of the customer that added the device " +
                "to match");
        assertEquals(customerName, deviceModel.getAddedByName(), "expected the name of the customer that " +
                "added the device to match");
        assertEquals(notes, deviceModel.getNotes(), "expected the device's notes to match");
    }

    @Test
    public void handleRequest_withMaintenanceFrequencyNull_createsAndSavesDevice() {
        // GIVEN
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(null);
        device.setManufacturerModel(manufacturerModel);
        device.setNextPmDueDate(null);
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN
        AddDeviceResult addDeviceResult = addDeviceActivity.handleRequest(addDeviceRequest);
        DeviceModel deviceModel = addDeviceResult.getDevice();

        // THEN
        verify(deviceDao).saveDevice(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        assertEquals(controlNumber, deviceModel.getControlNumber(), "expected the control number to match");
        assertEquals(serialNumber, deviceModel.getSerialNumber(), "expected the serial number to match");
        assertEquals(manufacturer, deviceModel.getManufacturer(), "expected the manufacturer to match");
        assertEquals(model, deviceModel.getModel(), "expected the model to match");
        assertEquals(manufactureDate, deviceModel.getManufactureDate(), "expected the manufacture date to match");
        assertEquals(ServiceStatus.IN_SERVICE.toString(), deviceModel.getServiceStatus(), "expected the " +
                "service status for the device to match");
        assertEquals(facilityName, deviceModel.getFacilityName(), "expected the facility name to match");
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment(), "expected the department to " +
                "match");
        assertEquals("", deviceModel.getComplianceThroughDate(), "expected the " +
                "compliance-through-date to match");
        assertEquals("", deviceModel.getLastPmCompletionDate(), "expected the last pm completion " +
                "date to match");
        assertEquals("", deviceModel.getNextPmDueDate(), "expected the next pm due " +
                "date to match");
        assertEquals(0, deviceModel.getMaintenanceFrequencyInMonths(), "expected " +
                "the maintenance frequency to match");
        assertEquals(LocalDate.now().toString(), deviceModel.getInventoryAddDate(), "expected the inventory " +
                "add date to match");
        assertEquals(customerId, deviceModel.getAddedById(), "expected the id of the customer that added the device " +
                "to match");
        assertEquals(customerName, deviceModel.getAddedByName(), "expected the name of the customer that " +
                "added the device to match");
        assertEquals(notes, deviceModel.getNotes(), "expected the device's notes to match");
    }

    @Test
    public void handleRequest_withRequiredValueNull_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(null)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a null value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withOptionalValueNull_createsAndSavesDevice() {
        // GIVEN
        device.setManufactureDate(null);
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(null)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);
        when(deviceDao.saveDevice(any(Device.class))).thenReturn(device);

        // WHEN
        AddDeviceResult addDeviceResult = addDeviceActivity.handleRequest(addDeviceRequest);
        DeviceModel deviceModel = addDeviceResult.getDevice();

        // THEN
        verify(deviceDao).saveDevice(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        assertEquals(controlNumber, deviceModel.getControlNumber(), "expected the control number to match");
        assertEquals(serialNumber, deviceModel.getSerialNumber(), "expected the serial number to match");
        assertEquals(manufacturer, deviceModel.getManufacturer(), "expected the manufacturer to match");
        assertEquals(model, deviceModel.getModel(), "expected the model to match");
        assertEquals("", deviceModel.getManufactureDate(), "expected the manufacture date to match");
        assertEquals(ServiceStatus.IN_SERVICE.toString(), deviceModel.getServiceStatus(), "expected the " +
                "service status for the device to match");
        assertEquals(facilityName, deviceModel.getFacilityName(), "expected the facility name to match");
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment(), "expected the department to " +
                "match");
        assertEquals("", deviceModel.getComplianceThroughDate(), "expected the " +
                "compliance-through-date to match");
        assertEquals("", deviceModel.getLastPmCompletionDate(), "expected the last pm completion " +
                "date to match");
        assertEquals(LocalDate.now().toString(), deviceModel.getNextPmDueDate(), "expected the next pm due " +
                "date to match");
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths(), "expected " +
                "the maintenance frequency to match");
        assertEquals(LocalDate.now().toString(), deviceModel.getInventoryAddDate(), "expected the inventory " +
                "add date to match");
        assertEquals(customerId, deviceModel.getAddedById(), "expected the id of the customer that added the device " +
                "to match");
        assertEquals(customerName, deviceModel.getAddedByName(), "expected the name of the customer that " +
                "added the device to match");
        assertEquals(notes, deviceModel.getNotes(), "expected the device's notes to match");
    }

    @Test
    public void handleRequest_withRequiredValueEmpty_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber("")
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected an empty value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withRequiredValueBlank_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber("   ")
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a blank value for serial number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withSerialNumberContainsAnInvalidCharacter_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber("1234-+")
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a serial number containing an invalid character to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withManufacturerModelDoesNotExist_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer("not a real manufacturer")
                .withModel("not a real model")
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
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
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withFacilityDepartmentDoesNotExist_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName("not a facility")
                .withAssignedDepartment("not a department")
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        doThrow(FacilityDepartmentNotFoundException.class)
                .when(facilityDepartmentDao).getFacilityDepartment(anyString(), anyString());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a facility/department not found to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withManufactureDateWrongFormat_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate("5-26-2023")
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected a manufacture date with incorrect format to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withFutureManufactureDate_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate futureDate = LocalDate.now().plusDays(1);
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(new LocalDateConverter().convert(futureDate))
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(null);
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString())).thenReturn(facilityDepartment);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected an add device request with a future manufacture date to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_devicePreviouslyAdded_throwsInvalidAttributeValueException() {
        // GIVEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(manufacturerModel);
        doThrow(DevicePreviouslyAddedException.class)
                .when(deviceDao).checkDevicePreviouslyAdded(any(ManufacturerModel.class), anyString());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                addDeviceActivity.handleRequest(addDeviceRequest),
                "Expected attempting to add a device with manufacturer, model, and serial number matching a " +
                        "a device previously added to result in an InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }
}
