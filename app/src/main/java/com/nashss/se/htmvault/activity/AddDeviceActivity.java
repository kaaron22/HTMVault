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
import com.nashss.se.htmvault.exceptions.DevicePreviouslyAddedException;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
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

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final ManufacturerModelDao manufacturerModelDao;
    private final FacilityDepartmentDao facilityDepartmentDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    /**
     * Instantiates a new Add device activity.
     *
     * @param deviceDao             the device dao
     * @param manufacturerModelDao  the manufacturer model dao
     * @param facilityDepartmentDao the facility department dao
     * @param metricsPublisher      the metrics publisher
     */
    @Inject
    public AddDeviceActivity(DeviceDao deviceDao, ManufacturerModelDao manufacturerModelDao,
                             FacilityDepartmentDao facilityDepartmentDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.manufacturerModelDao = manufacturerModelDao;
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Creates and adds the device per the request, with checks for valid input (i.e. the serial number is not null or
     * blank and contains only the characters allowed, the manufacturer/model combination is a valid one, etc.).
     * Throws an InvalidAttributeValueException if an input value is invalid.
     *
     * @param addDeviceRequest the add device request
     * @return the add device result, containing a public device model, converted from the DDB device
     */
    public AddDeviceResult handleRequest(final AddDeviceRequest addDeviceRequest) {
        log.info("Received AddDeviceRequest {}", addDeviceRequest);

        // validate the serial number in the request. it should not be null, blank, or empty. additionally, it should
        // contain alphanumeric characters, spaces, and dashes only
        String serialNumber = addDeviceRequest.getSerialNumber();
        validateRequestAttribute("Serial Number", serialNumber,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);

        // validate the manufacturer and model in the request. they should not be null, blank, or empty. additionally,
        // they should contain alphanumeric characters, spaces, and dashes only. finally, it should be an existing
        // manufacturer/model combination in the database
        String manufacturer = addDeviceRequest.getManufacturer();
        validateRequestAttribute("Manufacturer", manufacturer,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        String model = addDeviceRequest.getModel();
        validateRequestAttribute("Model", model, HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        ManufacturerModel manufacturerModel;
        try {
            manufacturerModel = manufacturerModelDao.getManufacturerModel(manufacturer, model);
        } catch (ManufacturerModelNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The manufacturer/model combination specified ({}/{}) while attempting to add a new " +
                    "device is not a valid combination", manufacturer, model);
            throw new InvalidAttributeValueException("Invalid manufacturer/model specified while attempting to add a " +
                    "new device to the inventory. " + e.getMessage());
        }
        // if the manufacturer/model retrieved from the database has a null maintenance frequency,
        // for our purposes, there is no maintenance required
        int requiredMaintenanceFrequencyInMonths =
                ifNull(manufacturerModel.getRequiredMaintenanceFrequencyInMonths(), 0);

        // verify that a device with matching manufacturer/model and serial number has not previously been added
        try {
            deviceDao.checkDevicePreviouslyAdded(manufacturerModel, serialNumber);
        } catch (DevicePreviouslyAddedException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("An attempt was made to add a duplicate device with manufacturer/model {}/{} and " +
                    "serial number {}", manufacturer, model, serialNumber);
            throw new InvalidAttributeValueException(e.getMessage());
        }

        // validate the facility and department in the request. they should not be null, blank, or empty. additionally,
        // they should contain alphanumeric characters, spaces, and dashes only. finally, it should be an existing
        // facility/department combination in the database
        String facilityName = addDeviceRequest.getFacilityName();
        validateRequestAttribute("Facility", facilityName,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        String assignedDepartment = addDeviceRequest.getAssignedDepartment();
        validateRequestAttribute("Assigned Department", assignedDepartment,
                HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH);
        try {
            facilityDepartmentDao.getFacilityDepartment(facilityName, assignedDepartment);
        } catch (FacilityDepartmentNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The facility/department combination specified ({}/{}) while attempting to add a new " +
                    "device is not a valid combination", facilityName, assignedDepartment);
            throw new InvalidAttributeValueException("Invalid facility/department specified while attempting to add " +
                    "a new device to the inventory. " + e.getMessage());
        }

        // ensure the optional manufacture date, if provided, has the correct format, and is not a future date
        String manufactureDate = addDeviceRequest.getManufactureDate();
        if (null != manufactureDate) {
            try {
                LocalDate manufactureDateParsed = new LocalDateConverter().unconvert(manufactureDate);
                if (manufactureDateParsed.isAfter(LocalDate.now())) {
                    metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                    log.info("The optional manufacture date provided while attempting to add a new device " +
                            "is a future date ({})", manufactureDateParsed);
                    throw new InvalidAttributeValueException(String.format("Cannot provide a future manufacture date " +
                            "(%s)", manufactureDateParsed));
                }
            } catch (DateTimeParseException e) {
                metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                log.info("The optional manufacture date provided while attempting to add a new device is not " +
                        "in the correct format of YYYY-MM-DD ({})", manufactureDate);
                throw new InvalidAttributeValueException("The optional manufacture date, if provided, must be " +
                        "formatted as YYYY-MM-DD");
            }
        }

        // valid request received - create and save new device
        metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);

        LocalDate dateOfAdd = LocalDate.now();
        Device device = new Device();
        device.setControlNumber(HTMVaultServiceUtils.generateId(HTMVaultServiceUtils.CONTROL_NUMBER_PREFIX,
                HTMVaultServiceUtils.CONTROL_NUMBER_LENGTH));
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(null == manufactureDate ? null : new LocalDateConverter().unconvert(manufactureDate));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(requiredMaintenanceFrequencyInMonths == 0 ? null : dateOfAdd);
        device.setInventoryAddDate(dateOfAdd);
        device.setAddedById(addDeviceRequest.getCustomerId());
        device.setAddedByName(addDeviceRequest.getCustomerName());
        device.setNotes(null == addDeviceRequest.getNotes() ? "" : addDeviceRequest.getNotes());

        Device savedDevice = deviceDao.saveDevice(device);

        return AddDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(savedDevice))
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
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The {} provided ({}) while attempting to add a new device contained invalid " +
                    "characters.", attributeName, attribute);
            throw new InvalidAttributeValueException(String.format("The %s provided (%s) while attempting to add a " +
                    "new device contained invalid characters.", attributeName, attribute));
        }
    }
}
