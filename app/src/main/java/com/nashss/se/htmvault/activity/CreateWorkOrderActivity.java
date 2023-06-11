package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Arrays;

public class CreateWorkOrderActivity {

    private final WorkOrderDao workOrderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public CreateWorkOrderActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public CreateWorkOrderResult handleRequest(final CreateWorkOrderRequest createWorkOrderRequest) {
        log.info("Received CreateWorkOrderRequest {}", createWorkOrderRequest);

        // verify the work order type is one of the types allowed

        boolean validWorkOrderType = false;
        for (WorkOrderType workOrderType : WorkOrderType.values()) {
            if (createWorkOrderRequest.getWorkOrderType().equals(workOrderType.toString())) {
                validWorkOrderType = true;
                break;
            }
        }
        if (!validWorkOrderType) {
            metricsPublisher.addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException("The work order type provided must be one of: " +
                    Arrays.toString(WorkOrderType.values()));
        }

        // verify the problem reported is not null or blank


        // if the request passes validation, create the new work order, with initial values set,
        // then save it to the database


        // convert the work order, build and return the result with the work order model
    }
}
