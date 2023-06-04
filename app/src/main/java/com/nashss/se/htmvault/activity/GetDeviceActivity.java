package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class GetDeviceActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    @Inject
    public GetDeviceActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetDeviceResult handleRequest(final GetDeviceRequest getDeviceRequest) {
        log.info("Received GetDeviceRequest {}", getDeviceRequest);

        String controlNumber = getDeviceRequest.getControlNumber();
        try {
            Device device = deviceDao.getDevice(controlNumber);
            metricsPublisher.addCount(MetricsConstants.GETDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);

            DeviceModel deviceModel = new ModelConverter().toDeviceModel(device);
            return GetDeviceResult.builder()
                    .withDeviceModel(deviceModel)
                    .build();
        } catch (DeviceNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.GETDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(e.getMessage());
        }
    }
}
