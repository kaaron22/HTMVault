package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class GetDeviceWorkOrdersActivity {

    private final DeviceDao deviceDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    @Inject
    public GetDeviceWorkOrdersActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetDeviceWorkOrdersResult handleRequest(final GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest) {
        log.info("Received GetDeviceWorkOrdersRequest {}", getDeviceWorkOrdersRequest);

        String controlNumber = getDeviceWorkOrdersRequest.getControlNumber();


    }
}
