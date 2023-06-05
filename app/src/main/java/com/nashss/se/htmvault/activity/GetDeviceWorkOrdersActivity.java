package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

public class GetDeviceWorkOrdersActivity {

    private final WorkOrderDao workOrderDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    @Inject
    public GetDeviceWorkOrdersActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetDeviceWorkOrdersResult handleRequest(final GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest) {
        log.info("Received GetDeviceWorkOrdersRequest {}", getDeviceWorkOrdersRequest);

        String controlNumber = getDeviceWorkOrdersRequest.getControlNumber();

        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        return GetDeviceWorkOrdersResult.builder()
                .withWorkOrders(new ModelConverter().toWorkOrderModels(workOrders))
                .build();
    }
}
