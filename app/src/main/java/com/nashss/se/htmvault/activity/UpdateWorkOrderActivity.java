package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.UpdateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.UpdateWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.exceptions.UpdateClosedWorkOrderException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderAwaitStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import javax.inject.Inject;

public class UpdateWorkOrderActivity {
    private final WorkOrderDao workorderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Update work order activity.
     *
     * @param workOrderDao     the work order dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public UpdateWorkOrderActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workorderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to update a work order.
     *
     * @param updateWorkOrderRequest the update work order request
     * @return the update work order result
     */
    public UpdateWorkOrderResult handleRequest(final UpdateWorkOrderRequest updateWorkOrderRequest) {
        log.info("Received UpdateWorkOrderRequest {}", updateWorkOrderRequest);

        if (null == updateWorkOrderRequest.getWorkOrderId() || updateWorkOrderRequest.getWorkOrderId().isBlank()) {
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("An attempt was made to update a work order, but the work order id was not provided or was blank");
            throw new InvalidAttributeValueException("A work order id must be provided in order to process the " +
                    "update work order request");
        }

        // obtain the work order from the database (throws WorkOrderNotFoundException if not found)
        WorkOrder workOrder;
        try {
            workOrder = workorderDao.getWorkOrder(updateWorkOrderRequest.getWorkOrderId());
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_WORKORDERNOTFOUND_COUNT, 0);
        } catch (WorkOrderNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
            log.info("A work order could not be found for work order id ({}) while attempting to perform a " +
                    "work order update", updateWorkOrderRequest.getWorkOrderId());
            throw new WorkOrderNotFoundException(String.format("Could not find a work order for work order id %s " +
                    e.getMessage(), updateWorkOrderRequest.getWorkOrderId()));
        }

        // verify the work order is not closed; if it is, throw an exception (closed work order are no longer
        // modifiable)
        if (workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.CLOSED) {
            throw new UpdateClosedWorkOrderException(String.format("Work order %s is closed and can no longer be " +
                    "modified", workOrder.getWorkOrderId()));
        }

        // verify the work order type is one of the types allowed
        boolean validWorkOrderType = false;
        if (!(null == updateWorkOrderRequest.getWorkOrderType())) {
            for (WorkOrderType workOrderType : WorkOrderType.values()) {
                if (updateWorkOrderRequest.getWorkOrderType().equals(workOrderType.toString())) {
                    validWorkOrderType = true;
                    break;
                }
            }
        }
        if (!validWorkOrderType) {
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException("The work order type provided must be one of: " +
                    Arrays.toString(WorkOrderType.values()));
        }

        // verify the optional work order await status is one of the types allowed (or empty/null)
        boolean validWorkOrderAwaitStatus = false;
        String workOrderAwaitStatusValue = updateWorkOrderRequest.getWorkOrderAwaitStatus();
        if (null == workOrderAwaitStatusValue || workOrderAwaitStatusValue.isBlank()) {
            workOrderAwaitStatusValue = null;
        }
        if (!(null == workOrderAwaitStatusValue)) {
            for (WorkOrderAwaitStatus workOrderAwaitStatus : WorkOrderAwaitStatus.values()) {
                if (updateWorkOrderRequest.getWorkOrderAwaitStatus().equals(workOrderAwaitStatus.toString())) {
                    validWorkOrderAwaitStatus = true;
                    break;
                }
            }
        } else {
            validWorkOrderAwaitStatus = true;
        }

        if (!validWorkOrderAwaitStatus) {
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException("The work order await status, if optionally provided, must be " +
                    "one of: " + Arrays.toString(WorkOrderAwaitStatus.values()));
        }

        // verify the problem reported is not null or blank
        String problemReported = updateWorkOrderRequest.getProblemReported();
        if (null == problemReported || problemReported.isBlank()) {
            metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException("The problem reported cannot be null or blank");
        }

        // verify the completion date time, if provided, has the correct format and is not a future date/time
        String completionDateTime = updateWorkOrderRequest.getCompletionDateTime();
        if (null == completionDateTime || completionDateTime.isBlank()) {
            completionDateTime = null;
        }
        if (null != completionDateTime) {
            try {
                LocalDateTime completionDateTimeParsed = new LocalDateTimeConverter().unconvert(completionDateTime);
                if (completionDateTimeParsed.isAfter(LocalDateTime.now())) {
                    metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
                    throw new InvalidAttributeValueException(String.format("Cannot provide a future completion date " +
                            "time (%s)", completionDateTimeParsed));
                }
            } catch (DateTimeParseException e) {
                metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
                throw new InvalidAttributeValueException("The date time provided must be formatted as " +
                        "YYYY-MM-DDTHH:MM:SS");
            }
        }

        // if the request passes validation, update the work order, then save it to the database
        metricsPublisher.addCount(MetricsConstants.UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 0);
        workOrder.setWorkOrderType(WorkOrderType.valueOf(updateWorkOrderRequest.getWorkOrderType()));
        workOrder.setWorkOrderAwaitStatus(null == workOrderAwaitStatusValue ? null :
                WorkOrderAwaitStatus.valueOf(workOrderAwaitStatusValue));
        workOrder.setProblemReported(updateWorkOrderRequest.getProblemReported());
        workOrder.setProblemFound(null == updateWorkOrderRequest.getProblemFound() ? null :
                updateWorkOrderRequest.getProblemFound());
        workOrder.setSummary(null == updateWorkOrderRequest.getSummary() ? null : updateWorkOrderRequest.getSummary());
        workOrder.setCompletionDateTime(null == completionDateTime ? null :
                new LocalDateTimeConverter().unconvert(updateWorkOrderRequest.getCompletionDateTime()));

        workorderDao.saveWorkOrder(workOrder);

        return UpdateWorkOrderResult.builder()
                .withWorkOrder(new ModelConverter().toWorkOrderModel(workOrder))
                .build();
    }
}
