package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import com.nashss.se.htmvault.models.DeviceModel;

import java.util.ArrayList;
import java.util.List;

import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.formatLocalDateTime;

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
                .withWorkOrderSummaries(convertWorkOrderSummaries(device.getWorkOrders()))
                .build();
    }

    /**
     * Private helper method to convert list of WorkOrderSummary objects to list of string lists, with each string list
     * containing the individual attributes of a WorkOrderSummary.
     * @param workOrderSummaries the list of WorkOrderSummary objects
     * @return the converted list
     */
    private List<List<String>> convertWorkOrderSummaries(List<WorkOrderSummary> workOrderSummaries) {
        List<List<String>> convertedWorkOrderSummaries = new ArrayList<>();

        for (WorkOrderSummary workOrderSummary : workOrderSummaries) {
            List<String> workOrderSummaryInfo = new ArrayList<>();
            workOrderSummaryInfo.add(workOrderSummary.getWorkOrderId());
            workOrderSummaryInfo.add(workOrderSummary.getWorkOrderType().toString());
            workOrderSummaryInfo.add(workOrderSummary.getCompletionStatus().toString());
            workOrderSummaryInfo.add(formatLocalDateTime(workOrderSummary.getDateTimeCreated()));
            workOrderSummaryInfo.add(null == workOrderSummary.getCompletionDateTime() ? "" :
                    formatLocalDateTime(workOrderSummary.getCompletionDateTime()));

            convertedWorkOrderSummaries.add(workOrderSummaryInfo);
        }

        return convertedWorkOrderSummaries;
    }

}
