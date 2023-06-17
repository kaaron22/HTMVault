package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.CloseWorkOrderRequest;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import com.nashss.se.htmvault.converters.LocalDateTimeConverter;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.CloseWorkOrderNotCompleteException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CloseWorkOrderActivity {

    private final WorkOrderDao workOrderDao;
    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public CloseWorkOrderActivity(WorkOrderDao workOrderDao, DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.deviceDao = deviceDao;
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

            // if it was a preventative maintenance or acceptance testing work order, update the last PM,
            // compliance through date, and next PM
            if (workOrder.getWorkOrderType() == WorkOrderType.ACCEPTANCE_TESTING ||
                    workOrder.getWorkOrderType() == WorkOrderType.PREVENTATIVE_MAINTENANCE) {
                LocalDate completionDate = LocalDate.of(completionDateTime.getYear(), completionDateTime.getMonth(),
                        completionDateTime.getDayOfMonth());
                updateDevice(workOrder.getControlNumber(), completionDate);
            }
        }

        return CloseWorkOrderResult.builder()
                .withWorkOrderModel(new ModelConverter().toWorkOrderModel(workOrder))
                .build();
    }

    private void updateDevice(String controlNumber, LocalDate completionDate) {
        Device device = deviceDao.getDevice(controlNumber);

        // if the device requires routine preventative maintenance, update the compliance-through-date to
        // 'maintenance frequency' number of months from the completion date, on the last day of that month,
        // and additionally update the next pm due date
        Integer maintenanceFrequency = device.getManufacturerModel().getRequiredMaintenanceFrequencyInMonths();
        if (maintenanceFrequency != null && maintenanceFrequency > 0) {
            // one month past the updated compliance month
            LocalDate complianceThroughDate = completionDate.plusMonths(maintenanceFrequency + 1);
            int month = complianceThroughDate.getMonthValue() - 1;
            int year = complianceThroughDate.getYear() - 1;
            // subtract days to reach the last day of the previous calendar month
            while(complianceThroughDate.getMonthValue() > month && complianceThroughDate.getYear() > year) {
                complianceThroughDate = complianceThroughDate.minusDays(1);
            }
            // if the proposed update to compliance-through-date is earlier than the existing compliance-through-date,
            // it remains the later date. otherwise, update it.
            if (!(null == device.getComplianceThroughDate())) {
                int comparison = complianceThroughDate.compareTo(device.getComplianceThroughDate());
                if (comparison > 0) {
                    device.setComplianceThroughDate(complianceThroughDate);
                }
            } else {
                device.setComplianceThroughDate(complianceThroughDate);
            }

            // update the next pm due date to 'maintenance frequency' number of months from the current pm due
            // date, or the compliance-through-date, whichever is sooner. this allows the normal schedule to be
            // maintained, unless the new compliance-through-date does not allow for it.
            //
            // for example, if the routine maintenance is normally done every 12 months in january, but the
            // maintenance was done in february because the device was in disrepair and awaiting parts until
            // then, the new compliance date would be the following february, but the next pm will still advance
            // to next january, so the department-based schedule is maintained.

            // one month past the updated compliance month
            LocalDate nextPmDate = device.getNextPmDueDate() == null ? device.getComplianceThroughDate() :
                    device.getNextPmDueDate().plusMonths(maintenanceFrequency + 1);
            month = nextPmDate.getMonthValue() - 1;
            year = nextPmDate.getYear() - 1;
            // subtract days to reach the last day of the previous calendar month
            while(nextPmDate.getMonthValue() > month && nextPmDate.getYear() > year) {
                nextPmDate = nextPmDate.minusDays(1);
            }

            int comparison = nextPmDate.compareTo(device.getComplianceThroughDate());

            if (comparison > 0) {
                nextPmDate = complianceThroughDate;
            }

            device.setNextPmDueDate(nextPmDate);
        }

        // now we can update the last pm completion date, if it's not sooner that the current last pm completion date
        int comparison = null == device.getLastPmCompletionDate() ? 1 :
                completionDate.compareTo(device.getLastPmCompletionDate());
        if (comparison > 0) {
            device.setLastPmCompletionDate(completionDate);
        }

        deviceDao.saveDevice(device);
    }
}
