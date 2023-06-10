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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private final String manufactureDate = "2023-05-26";
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
}