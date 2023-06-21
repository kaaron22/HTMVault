package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class GetDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    /**
     * Instantiates a new Get device activity.
     *
     * @param deviceDao        the device dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public GetDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to get a device from the database using the control number provided. Propagates a
     * DeviceNotFoundException thrown by DeviceDao.
     *
     * @param getDeviceRequest the get device request
     * @return the get device result
     */
    public GetDeviceResult handleRequest(final GetDeviceRequest getDeviceRequest) {
        log.info("Received GetDeviceRequest {}", getDeviceRequest);

        String controlNumber = getDeviceRequest.getControlNumber();

        Device device = deviceDao.getDevice(controlNumber);

        DeviceModel deviceModel = new ModelConverter().toDeviceModel(device);
        return GetDeviceResult.builder()
                .withDeviceModel(deviceModel)
                .build();
    }
}
