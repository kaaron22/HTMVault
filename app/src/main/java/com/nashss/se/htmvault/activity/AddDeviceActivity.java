package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.exceptions.InvalidAttributeException;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
import com.nashss.se.htmvault.utils.NullUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();

    @Inject
    public AddDeviceActivity(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public AddDeviceResult handleRequest(final AddDeviceRequest addDeviceRequest) {
        log.info("Received AddDeviceRequest {}", addDeviceRequest);

        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("Control Number", addDeviceRequest.getControlNumber());
        requiredRequestParameterValues.put("Serial Number", addDeviceRequest.getSerialNumber());
        requiredRequestParameterValues.put("Manufacturer", addDeviceRequest.getManufacturer());
        requiredRequestParameterValues.put("Model", addDeviceRequest.getModel());
        requiredRequestParameterValues.put("Facility Name", addDeviceRequest.getFacilityName());
        requiredRequestParameterValues.put("Assigned Department", addDeviceRequest.getAssignedDepartment());

        // ensures required values were provided in request; if any were not, an InvalidAttributeException is thrown
        NullUtils.ifNull((requiredRequestParameterValues));

        // ensures required values in request are not empty or blank; if any are, an InvalidAttributeException is thrown
        HTMVaultServiceUtils.ifEmptyOrBlank(requiredRequestParameterValues);


    }
}
