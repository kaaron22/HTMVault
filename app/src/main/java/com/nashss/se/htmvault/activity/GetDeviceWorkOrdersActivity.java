package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetDeviceWorkOrdersRequest;
import com.nashss.se.htmvault.activity.results.GetDeviceWorkOrdersResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderComparator;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.SortOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Arrays;
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

        String sortOrder = computeOrder(getDeviceWorkOrdersRequest.getSortOrder());

        String controlNumber = getDeviceWorkOrdersRequest.getControlNumber();

        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        if (sortOrder.equals(SortOrder.ASCENDING)) {
            workOrders.sort(new WorkOrderComparator());
        } else {
            workOrders.sort(new WorkOrderComparator().reversed());
        }

        return GetDeviceWorkOrdersResult.builder()
                .withWorkOrders(new ModelConverter().toWorkOrderModels(workOrders))
                .build();
    }

    private String computeOrder(String sortOrder) {
        String computedSortOrder = sortOrder;

        if (null == sortOrder) {
            computedSortOrder = SortOrder.DEFAULT;
        } else if (!Arrays.asList(SortOrder.values()).contains(sortOrder)) {
            metricsPublisher.addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(String.format("Unrecognized sort order: '%s'", sortOrder));
        }

        metricsPublisher.addCount(MetricsConstants.GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT, 0);
        return computedSortOrder;
    }
}
