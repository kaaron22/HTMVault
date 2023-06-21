package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.ReactivateDeviceRequest;
import com.nashss.se.htmvault.activity.results.ReactivateDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class ReactivateDeviceActivity {

    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Reactivate device activity.
     *
     * @param deviceDao        the device dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public ReactivateDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to reactivate/un-retire a device that was previously deactivated.
     *
     * @param reactivateDeviceRequest the reactivate device request
     * @return the reactivate device result
     */
    public ReactivateDeviceResult handleRequest(final ReactivateDeviceRequest reactivateDeviceRequest) {
        log.info("Received ReactivateDeviceRequest {}", reactivateDeviceRequest);

        String controlNumber = reactivateDeviceRequest.getControlNumber();

        // get device, if it exists
        Device device;
        try {
            device = deviceDao.getDevice(controlNumber);
            metricsPublisher.addCount(MetricsConstants.REACTIVATEDEVICE_DEVICENOTFOUND_COUNT, 0);
        } catch (DeviceNotFoundException e) {

            metricsPublisher.addCount(MetricsConstants.REACTIVATEDEVICE_DEVICENOTFOUND_COUNT, 1);
            log.info("An attempt was made to reactivate a device, but the device ({}) could not be found",
                    controlNumber);
            throw new DeviceNotFoundException(String.format("Unable to locate device %s while attempting to " +
                    "reactivate it", controlNumber));
        }

        device.setServiceStatus(ServiceStatus.IN_SERVICE);

        deviceDao.saveDevice(device);

        return ReactivateDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(device))
                .build();
    }
}
