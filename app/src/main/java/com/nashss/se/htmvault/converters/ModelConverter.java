package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import com.nashss.se.htmvault.models.DeviceModel;

import java.time.LocalDateTime;
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
                .withComplianceThroughDate(device.getComplianceThroughDate().toString())
                .withLastPmCompletionDate(device.getLastPmCompletionDate().toString())
                .withNextPmDueDate(device.getNextPmDueDate().toString())
                .withMaintenanceFrequencyInMonths(device.getMaintenanceFrequencyInMonths())
                .withInventoryAddDate(device.getInventoryAddDate().toString())
                .withAddedById(device.getAddedById())
                .withAddedByName(device.getAddedByName())
                .withNotes(device.getNotes())
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

    /**
     * Private helper method to format a LocalDateTime object to a String containing a LocalDate and LocalTime,
     * separated by a space
     * @param localDateTime the LocalDateTime to format as a String
     * @return the formatted String containing the date and time
     */
    private String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().toString() + " " + localDateTime.toLocalTime().toString();
    }
}
