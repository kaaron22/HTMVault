package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
import com.nashss.se.htmvault.utils.NullUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final ManufacturerModelDao manufacturerModelDao;
    private final FacilityDepartmentDao facilityDepartmentDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;
    private static final int MAX_MAINTENANCE_FREQUENCY = 24;

    @Inject
    public AddDeviceActivity(DeviceDao deviceDao, ManufacturerModelDao manufacturerModelDao,
                             FacilityDepartmentDao facilityDepartmentDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.manufacturerModelDao = manufacturerModelDao;
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    public AddDeviceResult handleRequest(final AddDeviceRequest addDeviceRequest) {
        log.info("Received AddDeviceRequest {}", addDeviceRequest);

        checkValidRequiredRequestParameters(addDeviceRequest);

        Device device = new Device();
        device.setControlNumber(addDeviceRequest.getControlNumber());
        device.setSerialNumber(addDeviceRequest.getSerialNumber());
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(addDeviceRequest.getManufacturer());
        manufacturerModel.setModel(addDeviceRequest.getModel());
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(null == addDeviceRequest.getManufactureDate() ? null :
                new LocalDateConverter().unconvert(addDeviceRequest.getManufactureDate()));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(addDeviceRequest.getFacilityName());
        device.setAssignedDepartment(addDeviceRequest.getAssignedDepartment());
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(addDeviceRequest.getMaintenanceFrequencyInMonths() == 0 ? null : LocalDate.now());
        device.setMaintenanceFrequencyInMonths(addDeviceRequest.getMaintenanceFrequencyInMonths());
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById(addDeviceRequest.getCustomerId());
        device.setAddedByName(addDeviceRequest.getCustomerName());
        device.setNotes(null == addDeviceRequest.getNotes() ? "" : addDeviceRequest.getNotes());
        device.setWorkOrders(new ArrayList<>());

        Device savedDevice = deviceDao.saveDevice(device);

        return new AddDeviceResult.Builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(savedDevice))
                .build();
    }

    private void checkValidRequiredRequestParameters(AddDeviceRequest addDeviceRequest) {
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("Control Number", addDeviceRequest.getControlNumber());
        requiredRequestParameterValues.put("Serial Number", addDeviceRequest.getSerialNumber());
        requiredRequestParameterValues.put("Manufacturer", addDeviceRequest.getManufacturer());
        requiredRequestParameterValues.put("Model", addDeviceRequest.getModel());
        requiredRequestParameterValues.put("Facility Name", addDeviceRequest.getFacilityName());
        requiredRequestParameterValues.put("Assigned Department", addDeviceRequest.getAssignedDepartment());

        try {
            // ensures required values were provided in request; if any were not, an InvalidAttributeValueException is
            // thrown
            NullUtils.ifNull((requiredRequestParameterValues));

            // ensures required values in request are not empty or blank; if any are, an InvalidAttributeValueException
            // is thrown
            HTMVaultServiceUtils.ifEmptyOrBlank(requiredRequestParameterValues);

            // ensures the value provided for the control number meets the requirement of containing alphanumeric
            // characters only
            HTMVaultServiceUtils.ifNotValidString("Control Number",
                    requiredRequestParameterValues.get("Control Number"), List.of(Character::isLetterOrDigit));

            // ensures the value provided for the serial number meets the requirement of containing alphanumeric
            // character or dashes only
            HTMVaultServiceUtils.ifNotValidString("Serial Number",
                    requiredRequestParameterValues.get("Serial Number"),
                    Arrays.asList(Character::isLetterOrDigit, character -> character.equals('-')));

            // check the manufacturer-model combination against the database to ensure it exists
            manufacturerModelDao.getManufacturerModel(addDeviceRequest.getManufacturer(), addDeviceRequest.getModel());

            // check the facility-department combination against the database to ensure it exists
            facilityDepartmentDao.getFacilityDepartment(addDeviceRequest.getFacilityName(),
                    addDeviceRequest.getAssignedDepartment());

            // ensure the optional manufacture date, if provided, has the correct format
            if (null != addDeviceRequest.getManufactureDate()) {
                new LocalDateConverter().unconvert(addDeviceRequest.getManufactureDate());
            }

            // ensure the maintenance frequency provided is non-negative and is no larger than the maximum frequency
            // allowed
            HTMVaultServiceUtils.allowedMaintenanceFrequencyRange(0, MAX_MAINTENANCE_FREQUENCY,
                    addDeviceRequest.getMaintenanceFrequencyInMonths());
        } catch (InvalidAttributeValueException |
                 ManufacturerModelNotFoundException |
                 FacilityDepartmentNotFoundException |
                 DateTimeParseException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(e.getMessage());
        }

        metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }
}
