package com.nashss.se.htmvault.activity;

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

    }

}
