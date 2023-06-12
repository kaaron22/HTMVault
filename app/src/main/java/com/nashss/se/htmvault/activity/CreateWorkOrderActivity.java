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

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class CreateWorkOrderActivity {

    private final DeviceDao deviceDao;
    private final WorkOrderDao workOrderDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public CreateWorkOrderActivity(DeviceDao deviceDao, WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public CreateWorkOrderResult handleRequest(final CreateWorkOrderRequest createWorkOrderRequest) {
        log.info("Received CreateWorkOrderRequest {}", createWorkOrderRequest);

        // verify the control number is for a device that exists and is found in the database
        String controlNumber = createWorkOrderRequest.getControlNumber();
        Device device;
        device = deviceDao.getDevice(controlNumber);

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
        LocalDateTime creationDateTime = LocalDateTime.now();
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
