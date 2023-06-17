package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.*;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.FacilityDepartments;
import com.nashss.se.htmvault.models.ManufacturerModels;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.utils.CollectionUtils;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.util.*;

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
                .withLastPmCompletionDate(null == device.getLastPmCompletionDate() ? "" :
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

    public List<DeviceModel> toDeviceModelList(List<Device> devices) {
        List<DeviceModel> deviceModelList = new ArrayList<>();
        for (Device device : devices) {
            deviceModelList.add(toDeviceModel(device));
        }
        return deviceModelList;
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

    public List<ManufacturerModels> toListManufacturerModels(Map<String, Set<String>> manufacturersAndModels) {
        List<ManufacturerModels> manufacturerModelsList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : manufacturersAndModels.entrySet()) {
            List<String> models = CollectionUtils.copyToList(entry.getValue());
            Collections.sort(models);
            ManufacturerModels manufacturerModels = ManufacturerModels.builder()
                    .withManufacturer(entry.getKey())
                    .withModels(models)
                    .build();
            manufacturerModelsList.add(manufacturerModels);
        }
        manufacturerModelsList.sort(new ManufacturerModelsComparator());
        return manufacturerModelsList;
    }

    public List<FacilityDepartments> toListFacilityDepartments(Map<String, Set<String>> facilitiesAndDepartments) {
        List<FacilityDepartments> facilityDepartmentsList = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : facilitiesAndDepartments.entrySet()) {
            List<String> departments = CollectionUtils.copyToList(entry.getValue());
            Collections.sort(departments);
            FacilityDepartments facilityDepartments = FacilityDepartments.builder()
                    .withFacility(entry.getKey())
                    .withDepartments(departments)
                    .build();
            facilityDepartmentsList.add(facilityDepartments);
        }
        facilityDepartmentsList.sort(new FacilityDepartmentsComparator());
        return facilityDepartmentsList;
    }
}
