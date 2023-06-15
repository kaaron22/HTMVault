package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.CloseWorkOrderNotCompleteException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;

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

        // retrieve work order from database (work order not found exception is thrown by dao if applicable)
        WorkOrder workOrder = workOrderDao.getWorkOrder(closeWorkOrderRequest.getWorkOrderId());

        // if the work order is already closed, there's nothing to do except return the result with the converted
        // work order
        if (!(workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.CLOSED)) {
            // ensure the work order information has been filled in for fields that are optional while the work order is
            // still open/ongoing, but which are required before the work order can be closed (i.e. completion date time
            // must be set, there must be a summary filled in, etc.)
            String problemFound = workOrder.getProblemFound();
            String summary = workOrder.getSummary();
            LocalDateTime completionDateTime = workOrder.getCompletionDateTime();
            if (null == problemFound || problemFound.isBlank() || null == summary || summary.isBlank()
                    || null == completionDateTime) {
                throw new CloseWorkOrderNotCompleteException("The work order information must be completed before " +
                        "permanently closing " + workOrder.getWorkOrderId());
            }

            // proceed to update the work order as closed
            // who closed it
            workOrder.setClosedById(closeWorkOrderRequest.getCustomerId());
            workOrder.setClosedByName(closeWorkOrderRequest.getCustomerName());

            // the time closed (now)
            LocalDateTime currentDateTime = LocalDateTime.now().minusHours(4);
            String currentDateTimeSerialized = new LocalDateTimeConverter().convert(currentDateTime);
            LocalDateTime currentDateTimeNoNanos = new LocalDateTimeConverter().unconvert(currentDateTimeSerialized);
            workOrder.setClosedDateTime(currentDateTimeNoNanos);

            // await status no longer applicable
            workOrder.setWorkOrderAwaitStatus(null);

            // close the work order
            workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);

            workOrder = workOrderDao.saveWorkOrder(workOrder);
        }

        return CloseWorkOrderResult.builder()
                .withWorkOrderModel(new ModelConverter().toWorkOrderModel(workOrder))
                .build();
    }
}
