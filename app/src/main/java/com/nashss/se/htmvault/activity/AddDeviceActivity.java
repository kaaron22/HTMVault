package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.exceptions.InvalidAttributeException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
import com.nashss.se.htmvault.utils.NullUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.*;

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();
    private MetricsPublisher metricsPublisher;

    @Inject
    public AddDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public AddDeviceResult handleRequest(final AddDeviceRequest addDeviceRequest) {
        log.info("Received AddDeviceRequest {}", addDeviceRequest);

        checkValidRequiredRequestParameters(addDeviceRequest);


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
            // ensures required values were provided in request; if any were not, an InvalidAttributeException is thrown
            NullUtils.ifNull((requiredRequestParameterValues));

            // ensures required values in request are not empty or blank; if any are, an InvalidAttributeException is
            // thrown
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
        } catch (InvalidAttributeException e) {
            metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeException(e.getMessage());
        }

        metricsPublisher.addCount(MetricsConstants.ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
    }
}
