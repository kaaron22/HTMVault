package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.RetireDeviceWithOpenWorkOrdersException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.inject.Inject;

public class RetireDeviceActivity {

    private final DeviceDao deviceDao;
    private final WorkOrderDao workOrderDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    /**
     * Instantiates a new Retire device activity.
     *
     * @param deviceDao        the device dao
     * @param workOrderDao     the work order dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public RetireDeviceActivity(DeviceDao deviceDao, WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to perform a soft delete of a device (moving it to an inactive status). If the device has
     * any work orders that are in an open status, a RetireDeviceWithOpenWorkOrdersException will be thrown. If the
     * device is not found for the provided control number, a DeviceNotFoundException is thrown.
     *
     * @param retireDeviceRequest the retire device request
     * @return the retire device result
     */
    public RetireDeviceResult handleRequest(final RetireDeviceRequest retireDeviceRequest) {
        log.info("Received RetireDeviceRequest {}", retireDeviceRequest);

        String controlNumber = retireDeviceRequest.getControlNumber();

        // get device, if it exists
        Device device;
        try {
            device = deviceDao.getDevice(controlNumber);
            metricsPublisher.addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 0);
        } catch (DeviceNotFoundException e) {
            log.info("An attempt was made to retire a device ({}) that could not be found in the database",
                    controlNumber);
            metricsPublisher.addCount(MetricsConstants.RETIREDEVICE_DEVICENOTFOUND_COUNT, 1);
            throw new DeviceNotFoundException(String.format("Device %s could not be found while attempting to " +
                    "retire/deactivate it", controlNumber));
        }

        // get device's work orders, if any
        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        // ensure none of the work orders are still open (if so, they need to be completed/closed first)
        for (WorkOrder workOrder : workOrders) {
            if (workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.OPEN) {
                metricsPublisher.addCount(MetricsConstants.RETIREDEVICE_WORKORDERSOPEN_COUNT, 1);
                log.info("A request was made to retire a device ({}), but it has at least one work order " +
                        "({}) that has not yet been completed/closed, so it could not be retired", device, workOrder);
                throw new RetireDeviceWithOpenWorkOrdersException("Work order " + workOrder.getWorkOrderId() +
                        " has not yet been completed/closed. All work orders for device (" + controlNumber + ") must " +
                        "be completed and closed before device can be retired");
            }
        }

        metricsPublisher.addCount(MetricsConstants.RETIREDEVICE_WORKORDERSOPEN_COUNT, 0);

        // if these conditions are met, we can proceed to perform a soft delete (update the service status to 'RETIRED')
        device.setServiceStatus(ServiceStatus.RETIRED);

        // save the device changes to the database
        deviceDao.saveDevice(device);

        // convert and return the device
        return RetireDeviceResult.builder()
                .withDeviceModel(new ModelConverter().toDeviceModel(device))
                .build();
    }
}
