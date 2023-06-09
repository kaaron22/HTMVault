package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateDeviceRequest;
import com.nashss.se.htmvault.activity.results.UpdateDeviceResult;
import com.nashss.se.htmvault.converters.LocalDateConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.exceptions.UpdateRetiredDeviceException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;

public class UpdateDeviceActivity {

    private final DeviceDao deviceDao;
    private final ManufacturerModelDao manufacturerModelDao;
    private final FacilityDepartmentDao facilityDepartmentDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Update device activity.
     *
     * @param deviceDao             the device dao
     * @param manufacturerModelDao  the manufacturer model dao
     * @param facilityDepartmentDao the facility department dao
     * @param metricsPublisher      the metrics publisher
     */
    @Inject
    public UpdateDeviceActivity(DeviceDao deviceDao, ManufacturerModelDao manufacturerModelDao,
                                FacilityDepartmentDao facilityDepartmentDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.manufacturerModelDao = manufacturerModelDao;
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to update a device's editable information, verifying that the input provided
     * in the request contain valid information (i.e. not null or blank).
     * Throws an InvalidAttributeValueException for invalid attributes.
     * Throws a
     *
     * @param updateDeviceRequest the update device request
     * @return the update device result
     */
    public UpdateDeviceResult handleRequest(final UpdateDeviceRequest updateDeviceRequest) {
        log.info("Received UpdateDeviceRequest {}", updateDeviceRequest);

        if (null == updateDeviceRequest.getControlNumber() || updateDeviceRequest.getControlNumber().isBlank()) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The control number provided ({}) while attempting to update a device contained " +
                            "invalid characters.", updateDeviceRequest.getControlNumber());
            throw new InvalidAttributeValueException("A device id (control number) must be provided in order to " +
                    "update a device.");
        }

        // verify the device being updated exists and is found in the database
        Device device;
        try {
            device = deviceDao.getDevice(updateDeviceRequest.getControlNumber());
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_DEVICENOTFOUND_COUNT, 0);
        } catch (DeviceNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_DEVICENOTFOUND_COUNT, 1);
            log.info("An attempt was made to update a device ({}) that could not be found.",
                    updateDeviceRequest.getControlNumber());
            throw new DeviceNotFoundException(String.format("The device with id %s being updated could not be found.",
                    updateDeviceRequest.getControlNumber()));
        }

        if (device.getServiceStatus() == ServiceStatus.RETIRED) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_DEVICERETIRED_COUNT, 1);
            log.info("A device update was attempted for a device ({}) that is inactive/retired.",
                    device.getControlNumber());
            throw new UpdateRetiredDeviceException("Cannot update a retired device: " + device.getControlNumber() +
                    ".");
        }
        metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_DEVICERETIRED_COUNT, 0);

        // validate the serial number in the request. it should not be null, blank, or empty. additionally, it should
        // contain alphanumeric characters, spaces, and dashes only
        String serialNumber = updateDeviceRequest.getSerialNumber();
        validateRequestAttribute("Serial Number", serialNumber,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);

        // validate the manufacturer and model in the request. they should not be null, blank, or empty. additionally,
        // they should contain alphanumeric characters, spaces, and dashes only. finally, it should be an existing
        // manufacturer/model combination in the database
        String manufacturer = updateDeviceRequest.getManufacturer();
        validateRequestAttribute("Manufacturer", manufacturer,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        String model = updateDeviceRequest.getModel();
        validateRequestAttribute("Model", model, HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        ManufacturerModel manufacturerModel = null;
        try {
            manufacturerModel = manufacturerModelDao.getManufacturerModel(manufacturer, model);
        } catch (ManufacturerModelNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The manufacturer/model combination specified ({}/{}) while attempting to update a " +
                    "device is not a valid combination.", manufacturer, model);
            throw new InvalidAttributeValueException("Invalid manufacturer/model specified while attempting to " +
                    "update a device in the inventory. " + e.getMessage());
        }
        int requiredMaintenanceFrequencyInMonths =
                ifNull(manufacturerModel.getRequiredMaintenanceFrequencyInMonths(), 0);

        // validate the facility and department in the request. they should not be null, blank, or empty. additionally,
        // they should contain alphanumeric characters, spaces, and dashes only. finally, it should be an existing
        // facility/department combination in the database
        String facilityName = updateDeviceRequest.getFacilityName();
        validateRequestAttribute("Facility", facilityName,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        String assignedDepartment = updateDeviceRequest.getAssignedDepartment();
        validateRequestAttribute("Assigned Department", assignedDepartment,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        try {
            facilityDepartmentDao.getFacilityDepartment(facilityName, assignedDepartment);
        } catch (FacilityDepartmentNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The facility/department combination specified ({}/{}) while attempting to update a " +
                    "device is not a valid combination.", facilityName, assignedDepartment);
            throw new InvalidAttributeValueException("Invalid facility/department specified while attempting to " +
                    "update a device in the inventory. " + e.getMessage());
        }

        // ensure the optional manufacture date, if provided, has the correct format, and is not a future date
        String manufactureDate = updateDeviceRequest.getManufactureDate();
        if (null != manufactureDate) {
            try {
                LocalDate manufactureDateParsed = new LocalDateConverter().unconvert(manufactureDate);
                if (manufactureDateParsed.isAfter(LocalDate.now())) {
                    metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                    log.info("The optional manufacture date provided while attempting to update a device " +
                            "is a future date ({}).", manufactureDateParsed);
                    throw new InvalidAttributeValueException(String.format("Cannot provide a future manufacture date " +
                            "(%s) when updating a device.", manufactureDateParsed));
                }
            } catch (DateTimeParseException e) {
                metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                log.info("The optional manufacture date provided while attempting to update a device is " +
                        "not in the correct format of YYYY-MM-DD ({}).", manufactureDate);
                throw new InvalidAttributeValueException("The date provided must be formatted as YYYY-MM-DD when " +
                        "submitting a request to update a device, but was: " + manufactureDate + ".");
            }
        }

        metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);

        // valid request received - update device and save changes
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(null == manufactureDate ? null : new LocalDateConverter().unconvert(manufactureDate));
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setNotes(null == updateDeviceRequest.getNotes() ? "" : updateDeviceRequest.getNotes());

        // if the updated maintenance frequency is 0 (no pm required), update the compliance through date and
        // the next pm due date to null
        if (requiredMaintenanceFrequencyInMonths == 0) {
            device.setComplianceThroughDate(null);
            device.setNextPmDueDate(null);
        // otherwise, update the compliance through date based on the last pm completion date, if there was one
        } else {
            if (!(null == device.getLastPmCompletionDate())) {
                // set the next compliance through date to the last day of the month, "maintenance frequency" number of
                // months after the month in which the last pm was completed

                // one month past the updated compliance month
                LocalDate updatedComplianceThroughDate =
                        device.getLastPmCompletionDate()
                                .plusMonths(requiredMaintenanceFrequencyInMonths + 1);
                int month = updatedComplianceThroughDate.getMonthValue() - 1;
                int year = updatedComplianceThroughDate.getYear() - 1;
                // subtract days to reach the last day of the previous calendar month
                while (updatedComplianceThroughDate.getMonthValue() > month &&
                        updatedComplianceThroughDate.getYear() > year) {
                    updatedComplianceThroughDate = updatedComplianceThroughDate.minusDays(1);
                }
                device.setComplianceThroughDate(updatedComplianceThroughDate);

                // if no pm scheduled, schedule it in sync with the compliance
                if (null == device.getNextPmDueDate()) {
                    device.setNextPmDueDate(device.getComplianceThroughDate());
                // otherwise, if the next pm due date is already prior to the new compliance through date (or equal
                // to it), we don't need to modify it; however, if it will now be late, we need to adjust it to the
                // compliance through date (scheduled sooner, so it's not late)
                } else {
                    int comparison = device.getNextPmDueDate().compareTo(device.getComplianceThroughDate());
                    if (comparison > 0) {
                        device.setNextPmDueDate(device.getComplianceThroughDate());
                    }
                }
            // a pm is required, but has never been done, so it's been due since the add date
            } else {
                device.setComplianceThroughDate(null);
                device.setNextPmDueDate(device.getInventoryAddDate());
            }
        }

        Device updatedDevice = deviceDao.saveDevice(device);

        return UpdateDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(updatedDevice))
                .build();
    }

    /**
     * Ensures a given attribute only contains the characters allowed.
     *
     * @param attributeName the actual attribute name (i.e. "serialNumber")
     * @param attribute the value of the attribute (i.e. M1722, G-33143, etc.)
     * @param validCharacterPattern a regex formatted string for pattern matching using the String matches method
     */
    private void validateRequestAttribute(String attributeName, String attribute, String validCharacterPattern) {
        if (!HTMVaultServiceUtils.isValidString(attribute, validCharacterPattern)) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The {} provided ({}) while attempting to update a device contained invalid characters.",
                    attributeName, attribute);
            throw new InvalidAttributeValueException(String.format("The %s provided (%s) while attempting to update " +
                    "a device contained invalid characters.", attributeName, attribute));
        }
    }

}
