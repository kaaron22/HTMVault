package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.UpdateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.UpdateClosedWorkOrderException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
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

        // obtain the work order from the database
        WorkOrder workOrder = workorderDao.getWorkOrder(updateWorkOrderRequest.getWorkOrderId());

        // verify the work order is not closed; if it is, throw an exception (closed work order are no longer
        // modifiable)
        if (workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.CLOSED) {
            throw new UpdateClosedWorkOrderException(String.format("Work order %s is closed and can no longer be " +
                    "modified", workOrder.getWorkOrderId()));
        }

    }
}
