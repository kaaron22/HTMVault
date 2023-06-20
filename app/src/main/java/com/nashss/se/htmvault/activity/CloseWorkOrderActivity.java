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
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;

public class CloseWorkOrderActivity {

    private final WorkOrderDao workOrderDao;
    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Close work order activity.
     *
     * @param workOrderDao     the work order dao
     * @param deviceDao        the device dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public CloseWorkOrderActivity(WorkOrderDao workOrderDao, DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.workOrderDao = workOrderDao;
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Processes a request to close a work order, including advancing the maintenance stats (i.e. the next pm due date),
     * if applicable. If the work order is not found or is incomplete, throws corresponding exceptions.
     *
     * @param closeWorkOrderRequest the close work order request
     * @return the close work order result
     */
    public CloseWorkOrderResult handleRequest(final CloseWorkOrderRequest closeWorkOrderRequest) {
        log.info("Received CloseWorkOrderRequest {}", closeWorkOrderRequest);

        // retrieve work order from database (work order not found exception is thrown by dao if applicable)
        WorkOrder workOrder;
        try {
            workOrder = workOrderDao.getWorkOrder(closeWorkOrderRequest.getWorkOrderId());
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTFOUND_COUNT, 0);
        } catch (WorkOrderNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
            log.info("A request was made to close a work order ({}) that could not be found",
                    closeWorkOrderRequest.getWorkOrderId());
            throw new WorkOrderNotFoundException("Attempted to close a work order that was not found " +
                    e.getMessage());
        }

        // if the work order is already closed, there's nothing to do except return the result with the converted
        // work order
        if (!(workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.CLOSED)) {
            // ensure the work order information has been filled in for fields that are optional while the work order is
            // still open/ongoing, but which are required before the work order can be closed (i.e. completion date time
            // must be set, there must be a summary filled in, etc.)
            String problemFound = workOrder.getProblemFound();
            String summary = workOrder.getSummary();
            LocalDateTime completionDateTime = workOrder.getCompletionDateTime();
            if (null == problemFound || problemFound.isBlank() || null == summary || summary.isBlank() ||
                    null == completionDateTime) {
                metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTCOMPLETE_COUNT, 1);
                log.info("An attempt was made to close a work order ({}) that was not yet completed",
                        workOrder.getWorkOrderId());
                throw new CloseWorkOrderNotCompleteException("The work order information must be completed before " +
                        "permanently closing " + workOrder.getWorkOrderId());
            }

            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTCOMPLETE_COUNT, 0);
            // proceed to update the work order as closed
            // the user who closed it
            workOrder.setClosedById(closeWorkOrderRequest.getCustomerId());
            workOrder.setClosedByName(closeWorkOrderRequest.getCustomerName());

            // the time closed (now)
            LocalDateTime currentDateTime = LocalDateTime.now().minusHours(4);
            String currentDateTimeSerialized = new LocalDateTimeConverter().convert(currentDateTime);
            LocalDateTime currentDateTimeNoNanos = new LocalDateTimeConverter().unconvert(currentDateTimeSerialized);
            workOrder.setClosedDateTime(currentDateTimeNoNanos);

            // await status no longer applicable (i.e. awaiting parts, awaiting performance check, etc.)
            workOrder.setWorkOrderAwaitStatus(null);

            // close the work order
            workOrder.setWorkOrderCompletionStatus(WorkOrderCompletionStatus.CLOSED);

            workOrder = workOrderDao.saveWorkOrder(workOrder);

            // update the maintenance dates, if applicable (i.e. the routine preventative maintenance has
            // been completed and the next pm due date and compliance-through-date can be advanced to the
            // next cycle)
            advanceMaintenanceStatsWithWorkOrderIfApplicable(workOrder.getWorkOrderId());
        }

        return CloseWorkOrderResult.builder()
                .withWorkOrderModel(new ModelConverter().toWorkOrderModel(workOrder))
                .build();
    }

    /**
     * Advances maintenance stats for the device by checking the database information on thework order specified by
     * the provided work order id.
     *
     * If the work order is a preventative maintenance, or the initial acceptance test, the last
     * pm completion date, compliance-through-date, and next pm due date are set/advanced. A repair work order
     * is dismissed for the purposes of this check.
     *
     * If the work order would roll back a particular one of these values, the original value is retained,
     * while the other values, if any would advance, are updated.
     *
     * If maintenance is not required for this device (i.e. maintenance frequency for the manufacturer/model is '0'
     * or null), the last pm completion date.
     *
     * Finally, the next pm due dates are only advanced to the next cycle if the normal schedule can be kept. For
     * example, if a PM is done every January, but a PM work order is then completed off-schedule in June (i.e. as a
     * manufacturer requirement following a repair), the compliance-through-date will advance to next June, but the
     * next pm, which was scheduled for next January, will be retained.
     *
     * The user will be able manually adjust to next June through a separate process, if so desired,
     * but this process is intended for automation and the device will typically remain on schedule with the rest of
     * the devices in the department.
     *
     * @param workOrderId the work order id of the work order to check for potential updates to the device's
     *                    maintenance stats
     * @return the device with updated compliance-through-date, next pm due date, and last pm completion date,
     * if applicable
     */
    public Device advanceMaintenanceStatsWithWorkOrderIfApplicable(String workOrderId) {
        // we limit this attempt based on actual work order information in the database, to prevent unofficial
        // work order information from affecting the maintenance stats
        WorkOrder workOrder;
        try {
            workOrder = workOrderDao.getWorkOrder(workOrderId);
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTFOUND_COUNT, 0);
        } catch (WorkOrderNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
            log.info("Could not find work order ({}) while attempting to update maintenance stats",
                    workOrderId);
            throw new WorkOrderNotFoundException("Unable to find the work order " + workOrderId + "while attempting " +
                    "to update maintenance stats");
        }

        // the device to which this work order pertains, which will potentially be updated
        Device device;
        try {
            device = deviceDao.getDevice(workOrder.getControlNumber());
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_DEVICENOTFOUND_COUNT, 0);
        } catch (DeviceNotFoundException e) {
            metricsPublisher.addCount(MetricsConstants.CLOSEWORKORDER_DEVICENOTFOUND_COUNT, 1);
            log.info("Could not find device ({}) while attempting to update maintenance stats",
                    workOrder.getControlNumber());
            throw new DeviceNotFoundException("Unable to find the device for the provided work order while " +
                    "attempting to update maintenance stats");
        }

        // if this is not a closed Preventative Maintenance or Acceptance Test work order, there's nothing to update
        if (!(workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.CLOSED)) {
            return device;
        }

        if (!(workOrder.getWorkOrderType() == WorkOrderType.PREVENTATIVE_MAINTENANCE ||
                workOrder.getWorkOrderType() == WorkOrderType.ACCEPTANCE_TESTING)) {
            return device;
        }

        // if the device requires routine preventative maintenance, update the compliance-through-date to
        // 'maintenance frequency' number of months from the completion date of this work order, on the last day
        // of that month (unless it causes the compliance date to roll back to an earlier date than it already has)
        Integer maintenanceFrequency = device.getManufacturerModel().getRequiredMaintenanceFrequencyInMonths();
        if (maintenanceFrequency != null && maintenanceFrequency > 0) {

            // go to the month following the eventual updated compliance date and then subtract days until
            // we get to the last day of the potential new compliance month
            LocalDateTime completionDateTime = workOrder.getCompletionDateTime();
            LocalDate proposedComplianceThroughDate = LocalDate.of(completionDateTime.getYear(),
                    completionDateTime.getMonth(), completionDateTime.getDayOfMonth())
                    .plusMonths(maintenanceFrequency + 1);
            int month = proposedComplianceThroughDate.getMonthValue() - 1;
            int year = proposedComplianceThroughDate.getYear() - 1;
            while (proposedComplianceThroughDate.getMonthValue() > month &&
                    proposedComplianceThroughDate.getYear() > year) {
                proposedComplianceThroughDate = proposedComplianceThroughDate.minusDays(1);
            }

            // if the proposed update to compliance-through-date is earlier than the existing compliance-through-date,
            // we make no change. otherwise, update it.
            if (!(null == device.getComplianceThroughDate())) {
                int comparison = proposedComplianceThroughDate.compareTo(device.getComplianceThroughDate());
                if (comparison > 0) {
                    device.setComplianceThroughDate(proposedComplianceThroughDate);
                }
            // if there is no existing compliance-through-date, then there is no comparison to make, our proposed
            // date is the only one to consider
            } else {
                device.setComplianceThroughDate(proposedComplianceThroughDate);
            }

            // calculate the proposed next pm due date to 'maintenance frequency' number of months from the current next
            // pm due date (if not null). if that is at or before the compliance-through-date, we can proceed to update.
            // if, however, it would set the next pm to be due late, we make no change.
            //
            // for example, if the routine maintenance is normally done every 12 months in january, but the
            // maintenance was done a month after the due date in february (i.e. because the device was in disrepair
            // and awaiting parts until then), the new compliance date would be the following february, but the next pm
            // will still advance to next january, so the department-based schedule (every january) is maintained.
            //
            // however, as a different example, if the work order in question that we're basing potential changes on
            // is completed in mid-cycle (i.e. in June 2023 for some reason, even though one was already done at the
            // normal time in January 2023), we won't auto-advance the next pm due to the following June (2024), since
            // it's normally done with everything else in the given department in January. if desired, the user can
            // manually reschedule to another month up to and including the compliance-through-date of June 2024,
            // through a separate process to be implemented for manually updating a device's next pm due date

            // if the next pm due date is null (should not be the case if the device requires maintenance, but we will
            // be defensive), there is no comparison to make - we'll sync maintenance with the
            // compliance-through-date. otherwise, we'll calculate the proposed date and make the comparison
            if (null == device.getNextPmDueDate()) {
                device.setNextPmDueDate(device.getComplianceThroughDate());
            } else {
                LocalDate proposedNextPmDueDate;
                proposedNextPmDueDate = device.getNextPmDueDate().plusMonths(maintenanceFrequency + 1);
                month = proposedNextPmDueDate.getMonthValue() - 1;
                year = proposedNextPmDueDate.getYear() - 1;
                // subtract days to reach the last day of the previous calendar month
                while (proposedNextPmDueDate.getMonthValue() > month && proposedNextPmDueDate.getYear() > year) {
                    proposedNextPmDueDate = proposedNextPmDueDate.minusDays(1);
                }

                int comparison = proposedNextPmDueDate.compareTo(device.getComplianceThroughDate());
                if (comparison <= 0) {
                    // the proposed next pm due date would be on time, so we will advance it to the next cycle
                    device.setNextPmDueDate(proposedNextPmDueDate);
                }
                // otherwise it would be late, so we will retain the current next pm due date (making no change)
            }
        }

        // now we can update the last pm completion date, if it won't roll it back to an earlier date
        LocalDateTime completionDateTime = workOrder.getCompletionDateTime();
        LocalDate completionDate = LocalDate.of(completionDateTime.getYear(), completionDateTime.getMonth(),
                completionDateTime.getDayOfMonth());
        int comparison = null == device.getLastPmCompletionDate() ? 1 :
                completionDate.compareTo(device.getLastPmCompletionDate());
        if (comparison > 0) {
            device.setLastPmCompletionDate(completionDate);
        }

        return deviceDao.saveDevice(device);
    }
}
