package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.models.DeviceModel;

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
                .withWorkOrderSummaries(device.getWorkOrders())
                .build();
    }
}
