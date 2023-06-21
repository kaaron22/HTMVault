package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderCompletionDateTimeComparator;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.SortOrder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class GetDeviceWorkOrdersActivity {

    private final WorkOrderDao workOrderDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    /**
     * Instantiates a new Get device work orders activity.
     *
     * @param workOrderDao     the work order dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public GetDeviceWorkOrdersActivity(WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handle request get device work orders result.
     *
     * @param getDeviceWorkOrdersRequest the get device work orders request
     * @return the get device work orders result
     */
    public GetDeviceWorkOrdersResult handleRequest(final GetDeviceWorkOrdersRequest getDeviceWorkOrdersRequest) {
        log.info("Received GetDeviceWorkOrdersRequest {}", getDeviceWorkOrdersRequest);

        String sortOrder = computeOrder(getDeviceWorkOrdersRequest.getSortOrder());

        String controlNumber = getDeviceWorkOrdersRequest.getControlNumber();

        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        if (sortOrder.equals(SortOrder.ASCENDING)) {
            workOrders.sort(new WorkOrderCompletionDateTimeComparator());
        } else {
            workOrders.sort(new WorkOrderCompletionDateTimeComparator().reversed());
        }

        return GetDeviceWorkOrdersResult.builder()
                .withWorkOrders(new ModelConverter().toWorkOrderModels(workOrders))
                .build();
    }

    // from project template, modified for project
    /**
     * A helper method to check that the sort order for the resulting, updated list of work orders
     * that is to be returned, is a valid sort order. Throws an InvalidAttributeValueException if the sort
     * order is invalid, though a null sort order is handled as the default (descending), for cases when the
     * user does not explicitly provide one.
     *
     * @param sortOrder the sort order (i.e. descending or ascending by the work order's defined comparator)
     * @return the sort order to use, including descending by default if none selected
     */
    private String computeOrder(String sortOrder) {
        String computedSortOrder = sortOrder;

        if (null == sortOrder) {
            computedSortOrder = SortOrder.DEFAULT;
        } else if (!Arrays.asList(SortOrder.values()).contains(sortOrder)) {
            metricsPublisher.addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 1);
            log.info("The sort order specified ({}) while attempting to get a the device's work orders was " +
                            "invalid", sortOrder);
            throw new InvalidAttributeValueException(String.format("Unrecognized sort order (%s) while attempting to " +
                    "get a device's work orders.", sortOrder));
        }

        metricsPublisher.addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
        return computedSortOrder;
    }
}
