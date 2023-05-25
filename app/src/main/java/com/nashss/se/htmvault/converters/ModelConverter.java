package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.models.DeviceModel;

public class ModelConverter {

    public DeviceModel toDeviceModel(Device device) {
        DeviceModel.Builder builder = DeviceModel.builder()
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
                .withNotes(device.getNotes());


    }
}
