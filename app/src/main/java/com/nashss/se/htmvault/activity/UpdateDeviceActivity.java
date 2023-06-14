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

import javax.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;

public class UpdateDeviceActivity {

    private final DeviceDao deviceDao;
    private final ManufacturerModelDao manufacturerModelDao;
    private final FacilityDepartmentDao facilityDepartmentDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public UpdateDeviceActivity(DeviceDao deviceDao, ManufacturerModelDao manufacturerModelDao,
                                FacilityDepartmentDao facilityDepartmentDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.manufacturerModelDao = manufacturerModelDao;
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    public UpdateDeviceResult handleRequest(final UpdateDeviceRequest updateDeviceRequest) {
        log.info("Received UpdateDeviceRequest {}", updateDeviceRequest);

        // verify the device being updated exists and is found in the database
        Device device = deviceDao.getDevice(updateDeviceRequest.getControlNumber());

        if (device.getServiceStatus() == ServiceStatus.RETIRED) {
            throw new UpdateRetiredDeviceException("Cannot update a retired device");
        }

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
            throw new InvalidAttributeValueException(e.getMessage());
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
            throw new InvalidAttributeValueException(e.getMessage());
        }

        // ensure the optional manufacture date, if provided, has the correct format, and is not a future date
        String manufactureDate = updateDeviceRequest.getManufactureDate();
        if (null != manufactureDate) {
            try {
                LocalDate manufactureDateParsed = new LocalDateConverter().unconvert(manufactureDate);
                if (manufactureDateParsed.isAfter(LocalDate.now())) {
                    metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                    throw new InvalidAttributeValueException(String.format("Cannot provide a future manufacture date " +
                            "(%s)", manufactureDateParsed));
                }
            } catch (DateTimeParseException e) {
                metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
                throw new InvalidAttributeValueException("The date provided must be formatted as YYYY-MM-DD");
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
        // otherwise, update the compliance through date based on the last pm completion date, if there was one (if not,
        // the complianceThroughDate and nextPmDueDate were already set as needed during device creation)
        } else {
            if (!(null == device.getLastPmCompletionDate())) {
                // set the next compliance through date to the last day of the month, "maintenance frequency" number of
                // months after the month in which the last pm was completed
                int dayOfMonth = device.getLastPmCompletionDate().getDayOfMonth();
                LocalDate updatedComplianceThroughDate =
                        device.getLastPmCompletionDate()
                                .plusMonths(requiredMaintenanceFrequencyInMonths + 1)
                                .minusDays(dayOfMonth);
                device.setComplianceThroughDate(updatedComplianceThroughDate);
                // if the next pm due date is already prior to the new compliance through date, we don't need to modify
                // it; otherwise, we need to adjust it to the compliance through date
                if (device.getNextPmDueDate().isAfter(updatedComplianceThroughDate)) {
                    device.setNextPmDueDate(updatedComplianceThroughDate);
                }
            }
        }

        Device updatedDevice = deviceDao.saveDevice(device);

        return UpdateDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(updatedDevice))
                .build();
    }

    private void validateRequestAttribute(String attributeName, String attribute, String validCharacterPattern) {
        if (!HTMVaultServiceUtils.isValidString(attribute, validCharacterPattern)) {
            metricsPublisher.addCount(MetricsConstants.UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(String.format("The %s provided (%s) contained invalid " +
                    "characters.", attributeName, attribute));
        }
    }

}
