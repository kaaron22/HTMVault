package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

public class SearchDevicesActivity {

    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;

    public SearchDevicesActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    public SearchDevicesResult handleRequest(final SearchDevicesRequest searchDevicesRequest) {
        return null;
    }
}
