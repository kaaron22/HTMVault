package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.requests.UpdateDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.activity.results.UpdateDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.*;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.MockitoAnnotations.openMocks;

class UpdateDeviceActivityTest {

    @Mock
    private ManufacturerModelDao manufacturerModelDao;
    @Mock
    private FacilityDepartmentDao facilityDepartmentDao;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @InjectMocks
    private UpdateDeviceActivity updateDeviceActivity;

    Device device = new Device();
    private final String controlNumber = "123";
    private final String serialNumber = "G-456";
    private final String manufacturer = "a manufacturer";
    private final String model = "a model";
    private final ManufacturerModel manufacturerModel = new ManufacturerModel();
    private final String facilityName = "a hospital";
    private final String assignedDepartment = "ER";
    private final String notes = "some notes";
    private final String customerId = "227345";
    private final String customerName = "John Doe";

    @BeforeEach
    void setUp() {
        openMocks(this);
        DeviceDao deviceDao = new DeviceDao(dynamoDBMapper, metricsPublisher);
        updateDeviceActivity = new UpdateDeviceActivity(deviceDao, manufacturerModelDao, facilityDepartmentDao,
                metricsPublisher);
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        Integer maintenanceFrequencyInMonths = 12;
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
        device.setControlNumber(controlNumber);
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(LocalDate.of(2023, 5, 26));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(LocalDate.of(2023, 6, 1));
        device.setInventoryAddDate(LocalDate.of(2023, 6, 1));
        device.setAddedById(customerId);
        device.setAddedByName(customerName);
        device.setNotes(notes);
    }

    @Test
    public void handleRequest_withAllValuesValidNoPmYetDone_updatesAndSavesDeviceNoChangeToComplianceOrNextPmDue() {
        // GIVEN
        // an update request and expected updated device
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        LocalDate updatedManufactureDate = LocalDate.now();

        Device updatedDevice = new Device();
        updatedDevice.setControlNumber(controlNumber);
        updatedDevice.setSerialNumber(serialNumber + "updated");
        updatedDevice.setManufacturerModel(updatedManufacturerModel);
        updatedDevice.setServiceStatus(ServiceStatus.IN_SERVICE);
        updatedDevice.setFacilityName(facilityName + "updated");
        updatedDevice.setAssignedDepartment(assignedDepartment + "updated");
        updatedDevice.setManufactureDate(updatedManufactureDate);
        updatedDevice.setNotes(notes + "updated");
        updatedDevice.setComplianceThroughDate(null);
        updatedDevice.setLastPmCompletionDate(null);
        updatedDevice.setNextPmDueDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setInventoryAddDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setAddedById(customerId);
        updatedDevice.setAddedByName(customerName);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN
        UpdateDeviceResult updateDeviceResult = updateDeviceActivity.handleRequest(updateDeviceRequest);
        DeviceModel deviceModel = updateDeviceResult.getDevice();

        // THEN
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
        verify(dynamoDBMapper).save(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        DeviceTestHelper.assertDeviceEqualsDeviceModel(updatedDevice, deviceModel);
    }

    @Test
    public void handleRequest_withAllValuesValidPmDone_updatesAndSavesDeviceIncludingComplianceAndNextPmIfDueSooner() {
        // GIVEN
        // a device with a compliance and pm that will now be due sooner because the manufacturer-model
        // to which the device record is being updated, is due for preventative maintenance every 6 months,
        // instead of every 12
        device.setLastPmCompletionDate(LocalDate.of(2023, 6, 10));
        device.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        // the next pm can be set earlier than the compliance through date, but not after; when
        // the manufacturer-model for this device is 'corrected' to one that requires a pm every
        // 6 months instead of every 12, the compliance through date will update to 2023-12-31.
        // since the next pm due date will then be after the updated compliance through date, it will
        // need to be updated to no later than 2023-12-31
        device.setNextPmDueDate(LocalDate.of(2024, 3, 31));


        // an update request and expected updated device
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        LocalDate updatedManufactureDate = LocalDate.now();

        Device updatedDevice = new Device();
        updatedDevice.setControlNumber(controlNumber);
        updatedDevice.setSerialNumber(serialNumber + "updated");
        updatedDevice.setManufacturerModel(updatedManufacturerModel);
        updatedDevice.setServiceStatus(ServiceStatus.IN_SERVICE);
        updatedDevice.setFacilityName(facilityName + "updated");
        updatedDevice.setAssignedDepartment(assignedDepartment + "updated");
        updatedDevice.setManufactureDate(updatedManufactureDate);
        updatedDevice.setNotes(notes + "updated");
        updatedDevice.setComplianceThroughDate(LocalDate.of(2023, 12, 31));
        updatedDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 10));
        updatedDevice.setNextPmDueDate(LocalDate.of(2023, 12, 31));
        updatedDevice.setInventoryAddDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setAddedById(customerId);
        updatedDevice.setAddedByName(customerName);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN
        UpdateDeviceResult updateDeviceResult = updateDeviceActivity.handleRequest(updateDeviceRequest);
        DeviceModel deviceModel = updateDeviceResult.getDevice();

        // THEN
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
        verify(dynamoDBMapper).save(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        DeviceTestHelper.assertDeviceEqualsDeviceModel(updatedDevice, deviceModel);
    }

    @Test
    public void handleRequest_withAllValuesValidPmDone_updatesAndSavesDeviceButNotNextPmIfAlreadyDueSooner() {
        // GIVEN
        // a device with a compliance that will now be earlier because the manufacturer-model
        // to which the device record is being updated, is due for preventative maintenance every 6 months,
        // instead of every 12
        device.setLastPmCompletionDate(LocalDate.of(2023, 6, 10));
        device.setComplianceThroughDate(LocalDate.of(2024, 6, 30));
        // however, in this case, the next pm was already set to be performed sooner than the new
        // compliance through date; when the manufacturer-model for this device is 'corrected' to
        // one that requires a pm every 6 months instead of every 12, the compliance through date will
        // update to 2023-12-31. since the next pm due date will still be sooner than the updated
        // compliance through date, it will not need to be updated (should remain the same)
        device.setNextPmDueDate(LocalDate.of(2023, 10, 31));

        // an update request and expected updated device
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        LocalDate updatedManufactureDate = LocalDate.now();

        Device updatedDevice = new Device();
        updatedDevice.setControlNumber(controlNumber);
        updatedDevice.setSerialNumber(serialNumber + "updated");
        updatedDevice.setManufacturerModel(updatedManufacturerModel);
        updatedDevice.setServiceStatus(ServiceStatus.IN_SERVICE);
        updatedDevice.setFacilityName(facilityName + "updated");
        updatedDevice.setAssignedDepartment(assignedDepartment + "updated");
        updatedDevice.setManufactureDate(updatedManufactureDate);
        updatedDevice.setNotes(notes + "updated");
        updatedDevice.setComplianceThroughDate(LocalDate.of(2023, 12, 31));
        updatedDevice.setLastPmCompletionDate(LocalDate.of(2023, 6, 10));
        updatedDevice.setNextPmDueDate(LocalDate.of(2023, 10, 31));
        updatedDevice.setInventoryAddDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setAddedById(customerId);
        updatedDevice.setAddedByName(customerName);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN
        UpdateDeviceResult updateDeviceResult = updateDeviceActivity.handleRequest(updateDeviceRequest);
        DeviceModel deviceModel = updateDeviceResult.getDevice();

        // THEN
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
        verify(dynamoDBMapper).save(any(Device.class));
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        DeviceTestHelper.assertDeviceEqualsDeviceModel(updatedDevice, deviceModel);
    }

    @Test
    public void handleRequest_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        // an update request, including updated manufacturer/model and facility/department
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected device not found to result in a DeviceNotFoundException thrown");
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
    }

    @Test
    public void handleRequest_deviceRetired_throwsUpdateRetiredDeviceException() {
        // GIVEN
        // an update request, including updated manufacturer/model and facility/department
        device.setServiceStatus(ServiceStatus.RETIRED);

        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN & THEN
        assertThrows(UpdateRetiredDeviceException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected device not found to result in a UpdateRetiredDeviceException thrown");
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
    }

    @Test
    public void handleRequest_withRequiredValueNull_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(null)
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a null value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withOptionalValueNull_createsAndSavesDevice() {
        // GIVEN
        // an update request and expected updated device
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        Device updatedDevice = new Device();
        updatedDevice.setControlNumber(controlNumber);
        updatedDevice.setSerialNumber(serialNumber + "updated");
        updatedDevice.setManufacturerModel(updatedManufacturerModel);
        updatedDevice.setServiceStatus(ServiceStatus.IN_SERVICE);
        updatedDevice.setFacilityName(facilityName + "updated");
        updatedDevice.setAssignedDepartment(assignedDepartment + "updated");
        updatedDevice.setManufactureDate(null);
        updatedDevice.setNotes(notes + "updated");
        updatedDevice.setComplianceThroughDate(null);
        updatedDevice.setLastPmCompletionDate(null);
        updatedDevice.setNextPmDueDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setInventoryAddDate(LocalDate.of(2023, 6, 1));
        updatedDevice.setAddedById(customerId);
        updatedDevice.setAddedByName(customerName);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(null)
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN
        UpdateDeviceResult updateDeviceResult = updateDeviceActivity.handleRequest(updateDeviceRequest);
        DeviceModel deviceModel = updateDeviceResult.getDevice();

        // THEN
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        verify(dynamoDBMapper).load(eq(Device.class), anyString());
        verify(dynamoDBMapper).save(any(Device.class));
        DeviceTestHelper.assertDeviceEqualsDeviceModel(updatedDevice, deviceModel);
    }

    @Test
    public void handleRequest_withRequiredValueEmpty_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber("")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);


        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected an empty value for control number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withRequiredValueBlank_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber("   ")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a blank value for serial number to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withSerialNumberContainsAnInvalidCharacter_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber("1234-+")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a serial number containing an invalid character to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withManufacturerModelDoesNotExist_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer("not a real manufacturer")
                .withModel("not a real model")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);
        doThrow(ManufacturerModelNotFoundException.class)
                .when(manufacturerModelDao).getManufacturerModel(anyString(), anyString());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a manufacturer/model not found to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withFacilityDepartmentDoesNotExist_throwsInvalidAttributeValueException() {
        // GIVEN
        LocalDate updatedManufactureDate = LocalDate.now();

        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(updatedManufactureDate.toString())
                .withFacilityName("not a facility")
                .withAssignedDepartment("not a department")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        doThrow(FacilityDepartmentNotFoundException.class)
                .when(facilityDepartmentDao).getFacilityDepartment(anyString(), anyString());

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a facility/department not found to result in an InvalidAttributeValueException " +
                        "thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withManufactureDateWrongFormat_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate("5-26-2023")
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(device);
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected a manufacture date with incorrect format to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }

    @Test
    public void handleRequest_withFutureManufactureDate_throwsInvalidAttributeValueException() {
        // GIVEN
        ManufacturerModel updatedManufacturerModel = new ManufacturerModel();
        updatedManufacturerModel.setManufacturer(manufacturer + "updated");
        updatedManufacturerModel.setModel(model + "updated");
        updatedManufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        FacilityDepartment updatedFacilityDepartment = new FacilityDepartment();
        updatedFacilityDepartment.setFacilityName(facilityName + "updated");
        updatedFacilityDepartment.setAssignedDepartment(assignedDepartment + "updated");

        LocalDate futureDate = LocalDate.now().plusDays(1);

        UpdateDeviceRequest updateDeviceRequest = UpdateDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber + "updated")
                .withManufacturer(manufacturer + "updated")
                .withModel(model + "updated")
                .withManufactureDate(futureDate.toString())
                .withFacilityName(facilityName + "updated")
                .withAssignedDepartment(assignedDepartment + "updated")
                .withNotes(notes + "updated")
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(device);
        when(manufacturerModelDao.getManufacturerModel(anyString(), anyString())).thenReturn(updatedManufacturerModel);
        when(facilityDepartmentDao.getFacilityDepartment(anyString(), anyString()))
                .thenReturn(updatedFacilityDepartment);

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        updateDeviceActivity.handleRequest(updateDeviceRequest),
                "Expected an add device request with a future manufacture date to result in an " +
                        "InvalidAttributeValueException thrown");
        verify(metricsPublisher).addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
    }
}