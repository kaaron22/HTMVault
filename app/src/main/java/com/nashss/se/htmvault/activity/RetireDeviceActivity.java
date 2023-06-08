package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.RetireDeviceRequest;
import com.nashss.se.htmvault.activity.results.RetireDeviceResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;

import com.nashss.se.htmvault.dynamodb.WorkOrderDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.RetireDeviceWithOpenWorkOrdersException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;


public class RetireDeviceActivity {

    private final DeviceDao deviceDao;
    private final WorkOrderDao workOrderDao;
    private final Logger log = LogManager.getLogger();
    private final MetricsPublisher metricsPublisher;

    @Inject
    public RetireDeviceActivity(DeviceDao deviceDao, WorkOrderDao workOrderDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.workOrderDao = workOrderDao;
        this.metricsPublisher = metricsPublisher;
    }

    public RetireDeviceResult handleRequest(final RetireDeviceRequest retireDeviceRequest) {
        log.info("Received RetireDeviceRequest {}", retireDeviceRequest);

        String controlNumber = retireDeviceRequest.getControlNumber();

        // get device, if it exists
        Device device = deviceDao.getDevice(controlNumber);

        // get device's work orders, if any
        List<WorkOrder> workOrders = workOrderDao.getWorkOrders(controlNumber);

        // ensure none of the work orders are still open (if so, they need to be completed/closed first)
        for (WorkOrder workOrder : workOrders) {
            if (workOrder.getWorkOrderCompletionStatus() == WorkOrderCompletionStatus.OPEN) {
                throw new RetireDeviceWithOpenWorkOrdersException("Work order " + workOrder.getWorkOrderId() +
                        " has not yet been completed/closed. All work orders for device (" + controlNumber + ") must " +
                        "be completed and closed before device can be retired");
            }
        }

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
