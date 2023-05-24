package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.exceptions.InvalidAttributeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class AddDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();

    @Inject
    public AddDeviceActivity(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public AddDeviceResult handleRequest(final AddDeviceRequest addDeviceRequest) {
        log.info("Received AddDeviceRequest {}", addDeviceRequest);

        // ensure required values were provided in request
        if (null == addDeviceRequest.getControlNumber() ||
                null == addDeviceRequest.getSerialNumber() ||
                null == addDeviceRequest.getManufacturer() ||
                null == addDeviceRequest.getModel() ||
                null == addDeviceRequest.getFacilityName() ||
                null == addDeviceRequest.getAssignedDepartment()) {
            throw new InvalidAttributeException("Values must be provided for:\n" +
                    "Control Number\n" +
                    "Serial Number\n" +
                    "Manufacturer\n" +
                    "Model\n" +
                    "Facility Name\n" +
                    "Assigned Department\n" +
                    addDeviceRequest);
        }

        // ensure required values in request are not empty
        if (addDeviceRequest.getControlNumber().isEmpty() ||
                addDeviceRequest.getSerialNumber().isEmpty() ||
                addDeviceRequest.getManufacturer().isEmpty() ||
                addDeviceRequest.getModel().isEmpty() ||
                addDeviceRequest.getFacilityName().isEmpty() ||
                addDeviceRequest.getAssignedDepartment().isEmpty()) {
            throw new InvalidAttributeException("Values provided cannot be empty for:\n" +
                    "Control Number\n" +
                    "Serial Number\n" +
                    "Manufacturer\n" +
                    "Model\n" +
                    "Facility Name\n" +
                    "Assigned Department\n" +
                    addDeviceRequest);
        }
    }

}
