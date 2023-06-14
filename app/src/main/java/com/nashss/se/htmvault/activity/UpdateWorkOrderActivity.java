package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.UpdateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateWorkOrderActivity {
    private final DeviceDao deviceDao;
    private final WorkOrderDao workorderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    public UpdateWorkOrderActivity(DeviceDao deviceDao, WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.workorderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public UpdateWorkOrderResult handleRequest(final UpdateWorkOrderRequest updateWorkOrderRequest) {
        log.info("Received UpdateWorkOrderRequest {}", updateWorkOrderRequest);


    }
}
