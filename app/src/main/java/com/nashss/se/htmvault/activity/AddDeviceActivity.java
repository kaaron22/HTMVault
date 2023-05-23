package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.AddDeviceRequest;
import com.nashss.se.htmvault.activity.results.AddDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
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


    }

}
