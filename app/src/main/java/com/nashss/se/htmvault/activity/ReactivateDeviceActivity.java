package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.ReactivateDeviceRequest;
import com.nashss.se.htmvault.activity.results.ReactivateDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class ReactivateDeviceActivity {

    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public ReactivateDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public ReactivateDeviceResult handleRequest(final ReactivateDeviceRequest reactivateDeviceRequest) {
        log.info("Received ReactivateDeviceRequest {}", reactivateDeviceRequest);

        String controlNumber = reactivateDeviceRequest.getControlNumber();

        // get device, if it exists
        Device device = deviceDao.getDevice(controlNumber);

        device.setServiceStatus(ServiceStatus.IN_SERVICE);

        deviceDao.saveDevice(device);

        return ReactivateDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(device))
                .build();
    }
}
