package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class CloseWorkOrderActivity {

    private final WorkOrderDao workOrderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public CloseWorkOrderActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public CloseWorkOrderResult handleRequest(final CloseWorkOrderRequest closeWorkOrderRequest) {
        log.info("Received CloseWorkOrderRequest {}", closeWorkOrderRequest);


    }
}
