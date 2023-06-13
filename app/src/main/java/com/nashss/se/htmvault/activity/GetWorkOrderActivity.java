package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.GetWorkOrderResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class GetWorkOrderActivity {

    private final WorkOrderDao workOrderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public GetWorkOrderActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetWorkOrderResult handleRequest(final GetWorkOrderRequest getWorkOrderRequest) {
        log.info("Received GetWorkOrderRequest {}", getWorkOrderRequest);

        WorkOrder workOrder = workOrderDao.getWorkOrder(getWorkOrderRequest.getWorkOrderId());

        return GetWorkOrderResult.builder()
                .withWorkOrderModel(new ModelConverter().toWorkOrderModel(workOrder))
                .build();
    }
}
