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

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final ManufacturerModelDao manufacturerModelDao;
    private final FacilityDepartmentDao facilityDepartmentDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

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
        ManufacturerModel manufacturerModel = null;
        try {
            manufacturerModel = manufacturerModelDao.getManufacturerModel(manufacturer, model);
        } catch (ManufacturerModelNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(e.getMessage());
        }
        int requiredMaintenanceFrequencyInMonths =
                ifNull(manufacturerModel.getRequiredMaintenanceFrequencyInMonths(), 0);

        // verify that a device with matching manufacturer/model and serial number has not previously been added
        try {
            deviceDao.checkDevicePreviouslyAdded(manufacturerModel, serialNumber);
        } catch (DevicePreviouslyAddedException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
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
            throw new InvalidAttributeValueException(e.getMessage());
        }

        // ensure the optional manufacture date, if provided, has the correct format, and is not a future date
        String manufactureDate = addDeviceRequest.getManufactureDate();
        if (null != manufactureDate) {
            try {
                LocalDate manufactureDateParsed = new LocalDateConverter().unconvert(manufactureDate);
                if (manufactureDateParsed.isAfter(LocalDate.now())) {
                    metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                    throw new InvalidAttributeValueException(String.format("Cannot provide a future manufacture date " +
                            "(%s)", manufactureDateParsed));
                }
            } catch (DateTimeParseException e) {
                metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                throw new InvalidAttributeValueException("The date provided must be formatted as YYYY-MM-DD");
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
        device.setWorkOrders(null);

        Device savedDevice = deviceDao.saveDevice(device);

        return AddDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(savedDevice))
                .build();
    }

    private void validateRequestAttribute(String attributeName, String attribute, String validCharacterPattern) {
        if (!HTMVaultServiceUtils.isValidString(attribute, validCharacterPattern)) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(String.format("The %s provided (%s) contained invalid " +
                            "characters.", attributeName, attribute));
        }
    }
}
