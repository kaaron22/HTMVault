package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.util.ArrayList;
import java.util.List;

public class ModelConverter {

    public DeviceModel toDeviceModel(Device device) {
        return DeviceModel.builder()
                .withControlNumber(device.getControlNumber())
                .withSerialNumber(device.getSerialNumber())
                .withManufacturer(device.getManufacturerModel().getManufacturer())
                .withModel(device.getManufacturerModel().getModel())
                .withManufactureDate(device.getManufactureDate() == null ? "" : device.getManufactureDate().toString())
                .withServiceStatus(device.getServiceStatus().toString())
                .withFacilityName(device.getFacilityName())
                .withAssignedDepartment(device.getAssignedDepartment())
                .withComplianceThroughDate(null == device.getComplianceThroughDate() ? "" :
                        device.getComplianceThroughDate().toString())
                .withLastPmCompletionDate(null == device.getComplianceThroughDate() ? "" :
                        device.getLastPmCompletionDate().toString())
                .withNextPmDueDate(null == device.getNextPmDueDate() ? "" : device.getNextPmDueDate().toString())
                .withMaintenanceFrequencyInMonths(null == device.getManufacturerModel()
                        .getRequiredMaintenanceFrequencyInMonths() ? 0 : device.getManufacturerModel()
                        .getRequiredMaintenanceFrequencyInMonths())
                .withInventoryAddDate(device.getInventoryAddDate().toString())
                .withAddedById(device.getAddedById())
                .withAddedByName(device.getAddedByName())
                .withNotes(null == device.getNotes() ? "" : device.getNotes())
                .build();
    }

    public WorkOrderModel toWorkOrderModel(WorkOrder workOrder) {
        return WorkOrderModel.builder()
                .withWorkOrderId(workOrder.getWorkOrderId())
                .withWorkOrderType(workOrder.getWorkOrderType().toString())
                .withControlNumber(workOrder.getControlNumber())
                .withSerialNumber(workOrder.getSerialNumber())
                .withWorkOrderCompletionStatus(workOrder.getWorkOrderCompletionStatus().toString())
                .withWorkOrderAwaitStatus(null == workOrder.getWorkOrderAwaitStatus() ? "" :
                        workOrder.getWorkOrderAwaitStatus().toString())
                .withManufacturer(workOrder.getManufacturerModel().getManufacturer())
                .withModel(workOrder.getManufacturerModel().getModel())
                .withFacilityName(workOrder.getFacilityName())
                .withAssignedDepartment(workOrder.getAssignedDepartment())
                .withProblemReported(workOrder.getProblemReported())
                .withProblemFound(null == workOrder.getProblemFound() ? "" : workOrder.getProblemFound())
                .withCreatedById(workOrder.getCreatedById())
                .withCreatedByName(workOrder.getCreatedByName())
                .withCreationDateTime(HTMVaultServiceUtils.formatLocalDateTime(workOrder.getCreationDateTime()))
                .withClosedById(null == workOrder.getClosedById() ? "" : workOrder.getClosedById())
                .withClosedByName(null == workOrder.getClosedByName() ? "" : workOrder.getClosedByName())
                .withClosedDateTime(null == workOrder.getClosedDateTime() ? "" :
                        HTMVaultServiceUtils.formatLocalDateTime(workOrder.getClosedDateTime()))
                .withSummary(null == workOrder.getSummary() ? "" : workOrder.getSummary())
                .withCompletionDateTime(null == workOrder.getCompletionDateTime() ? "" :
                        HTMVaultServiceUtils.formatLocalDateTime(workOrder.getCompletionDateTime()))
                .build();
    }

    public List<WorkOrderModel> toWorkOrderModels(List<WorkOrder> workOrders) {
        List<WorkOrderModel> workOrderModels = new ArrayList<>();

        for (WorkOrder workOrder : workOrders) {
            workOrderModels.add(toWorkOrderModel(workOrder));
        }

        return workOrderModels;
    }
}
