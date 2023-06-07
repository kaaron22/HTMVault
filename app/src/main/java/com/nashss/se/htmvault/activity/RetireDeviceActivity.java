package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;


public class RetireDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    @Inject
    public RetireDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public RetireDeviceResult handleRequest(final RetireDeviceRequest retireDeviceRequest) {
        log.info("Received RetireDeviceRequest {}", retireDeviceRequest);

        String controlNumber = retireDeviceRequest.getControlNumber();

        // get device, if it exists
        Device device = deviceDao.getDevice(controlNumber);

        // get device's work orders, if any

        // ensure none of the work orders are still open (if so, they need to be completed/closed first)

        // if these conditions are met, we can proceed to perform a soft delete (update the service status to 'RETIRED')

        // save the device changes to the database

        // convert and return the device
    }
}
