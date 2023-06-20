package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CreateWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CreateWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderCompletionDateTimeComparator;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.SortOrder;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class CreateWorkOrderActivity {

    private final DeviceDao deviceDao;
    private final WorkOrderDao workOrderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Create work order activity.
     *
     * @param deviceDao        the device dao
     * @param workOrderDao     the work order dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public CreateWorkOrderActivity(DeviceDao deviceDao, WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to create a new work order, with checks to verify the device to which this work
     * order is attached does exist, and that the inputs for creating the work order are valid (i.e. a valid
     * work order type, such as 'preventative maintenance' or 'repair'). Additionally, verifies that required
     * input is not null or blank.
     *
     * @param createWorkOrderRequest the create work order request
     * @return the create work order result
     */
    public CreateWorkOrderResult handleRequest(final CreateWorkOrderRequest createWorkOrderRequest) {
        log.info("Received CreateWorkOrderRequest {}", createWorkOrderRequest);

        // verify the control number is for a device that exists and is found in the database
        String controlNumber = createWorkOrderRequest.getControlNumber();
        Device device;
        try {
            device = deviceDao.getDevice(controlNumber);
        } catch (DeviceNotFoundException e) {
            throw new DeviceNotFoundException(e.getMessage());
        }

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
        String problemReported = createWorkOrderRequest.getProblemReported();
        if (null == problemReported || problemReported.isBlank()) {
            metricsPublisher.addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException("The problem reported cannot be null or blank");
        }

        // if the request passes validation, create the new work order, with initial values set,
        // then save it to the database
        metricsPublisher.addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 0);
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(HTMVaultServiceUtils.generateId("WR", 8));
        workOrder.setWorkOrderType(WorkOrderType.valueOf(createWorkOrderRequest.getWorkOrderType()));
        workOrder.setControlNumber(createWorkOrderRequest.getControlNumber());
        workOrder.setSerialNumber(device.getSerialNumber());
        workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrder.setWorkOrderAwaitStatus(null);
        workOrder.setManufacturerModel(device.getManufacturerModel());
        workOrder.setFacilityName(device.getFacilityName());
        workOrder.setAssignedDepartment(device.getAssignedDepartment());
        workOrder.setProblemReported(createWorkOrderRequest.getProblemReported());
        workOrder.setProblemFound(createWorkOrderRequest.getProblemFound());
        workOrder.setCreatedById(createWorkOrderRequest.getCreatedById());
        workOrder.setCreatedByName(createWorkOrderRequest.getCreatedByName());
        // creation date time of 'now' without nanos
        LocalDateTime creationDateTime = LocalDateTime.now().minusHours(4);
        String creationDateTimeConverted = new LocalDateTimeConverter().convert(creationDateTime);
        LocalDateTime creationDateTimeNoNanos = new LocalDateTimeConverter().unconvert(creationDateTimeConverted);
        workOrder.setCreationDateTime(creationDateTimeNoNanos);
        workOrder.setClosedById(null);
        workOrder.setClosedByName(null);
        workOrder.setClosedDateTime(null);
        workOrder.setSummary(null);
        workOrder.setCompletionDateTime(null);

        // obtain existing list of work orders, add new work order, and sort according to specified sort order; then
        // save the new work order. this is done instead of saving new work order, and then obtaining the "updated" list
        // of work orders, as it may not yet be updated with the new work order (eventually consistent database)
        String sortOrder = computeOrder(createWorkOrderRequest.getSortOrder());

        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        workOrders.add(workOrder);

        if (sortOrder.equals(SortOrder.ASCENDING)) {
            workOrders.sort(new WorkOrderCompletionDateTimeComparator());
        } else {
            workOrders.sort(new WorkOrderCompletionDateTimeComparator().reversed());
        }

        // save the new work order
        workOrderDao.saveWorkOrder(workOrder);

        // convert the work order, build and return the result with the work order model
        return CreateWorkOrderResult.builder()
                .withWorkOrderModels(new ModelConverter().toWorkOrderModels(workOrders))
                .build();
    }

    // from project template
    /**
     * A helper method to check that the sort order for the resulting, updated list of work orders
     * that is to be returned, is a valid sort order. Throws an exception if the sort order is invalid,
     * though a null sort order is handled as the default (descending), for cases when the user does
     * not explicitly provide one.
     *
     * @param sortOrder the sort order (i.e. descending or ascending by the work order's defined comparator)
     * @return the sort order to use, including descending by default if none selected
     */
    private String computeOrder(String sortOrder) {
        String computedSortOrder = sortOrder;

        if (null == sortOrder) {
            computedSortOrder = SortOrder.DEFAULT;
        } else if (!Arrays.asList(SortOrder.values()).contains(sortOrder)) {
            metricsPublisher.addCount(MetricsConstants.CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new InvalidAttributeValueException(String.format("Unrecognized sort order: '%s'", sortOrder));
        }

        return computedSortOrder;
    }
}
