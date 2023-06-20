package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartmentsComparator;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModelsComparator;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.FacilityDepartments;
import com.nashss.se.htmvault.models.ManufacturerModels;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.utils.CollectionUtils;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class with methods for converting backend DDB objects to public model objects,
 * for use by the frontend.
 */
public class ModelConverter {

    /**
     * Converts a DDB Device object to a public model version (DeviceModel).
     *
     * @param device the Device to convert
     * @return the converted Device (DeviceModel)
     */
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

    /**
     * Converts a list of DDB Device objects to a list of DeviceModel objects.
     *
     * @param devices the list of Devices to convert
     * @return the converted list DeviceModel objects
     */
    public List<DeviceModel> toDeviceModelList(List<Device> devices) {
        List<DeviceModel> deviceModelList = new ArrayList<>();
        for (Device device : devices) {
            deviceModelList.add(toDeviceModel(device));
        }
        return deviceModelList;
    }

    /**
     * Converts a DDB WorkOrder object to a public model version (WorkOrderModel).
     *
     * @param workOrder the work order
     * @return the work order model
     */
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

    /**
     * Converts a list of DDB WorOrder objects to a list of WorkOrderModel objects.
     *
     * @param workOrders the list of WorkOrder objects to convert
     * @return the converted list of WorkOrderModel objects
     */
    public List<WorkOrderModel> toWorkOrderModels(List<WorkOrder> workOrders) {
        List<WorkOrderModel> workOrderModels = new ArrayList<>();

        for (WorkOrder workOrder : workOrders) {
            workOrderModels.add(toWorkOrderModel(workOrder));
        }

        return workOrderModels;
    }

    /**
     * Converts a map of manufacturers, each with a set of models in a form that can be used
     * by the frontend (a list of manufacturers, each containing a list of associated models).
     *
     * @param manufacturersAndModels the map of manufacturer keys with model sets as values
     * @return the converted list
     */
    public List<ManufacturerModels> toListManufacturerModels(Map<String, Set<String>> manufacturersAndModels) {
        List<ManufacturerModels> manufacturerModelsList = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : manufacturersAndModels.entrySet()) {

            // converting the set of models for this manufacturer to a list of models
            List<String> models = CollectionUtils.copyToList(entry.getValue());

            // sort the models so that the front end, which uses this list to populate a drop-down
            // selection, lists the models in lexicographical order
            Collections.sort(models);

            // build the frontend object that contains a string manufacturer name and a list of string
            // model names
            ManufacturerModels manufacturerModels = ManufacturerModels.builder()
                    .withManufacturer(entry.getKey())
                    .withModels(models)
                    .build();

            // add it to the list
            manufacturerModelsList.add(manufacturerModels);
        }

        // sort the list of ManufacturerModels objects (by manufacturer name) so that the front end, which uses this
        // list to populate a drop-down selection, lists the manufacturers in lexicographical order (the manufacturer's
        // associated list of models is already sorted)
        manufacturerModelsList.sort(new ManufacturerModelsComparator());
        return manufacturerModelsList;
    }

    /**
     * Converts a list of individual FacilityDepartment objects to a list of FacilityDepartments that
     * can be iterated over by the frontend (a list of facilities, each containing a list of associated
     * departments).
     *
     * @param facilitiesAndDepartments the list of FacilityDepartment objects
     * @return the converted list
     */

    public List<FacilityDepartments> toListFacilityDepartments(Map<String, Set<String>> facilitiesAndDepartments) {
        List<FacilityDepartments> facilityDepartmentsList = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : facilitiesAndDepartments.entrySet()) {
            List<String> departments = CollectionUtils.copyToList(entry.getValue());

            // sort the departments so that the front end, which uses this list to populate a drop-down
            // selection, lists the departments in lexicographical order (the facility's associated list of models
            // is already sorted)
            Collections.sort(departments);
            FacilityDepartments facilityDepartments = FacilityDepartments.builder()
                    .withFacility(entry.getKey())
                    .withDepartments(departments)
                    .build();
            facilityDepartmentsList.add(facilityDepartments);
        }

        // sort (by facility name) the facilities so that the front end, which uses this list to populate a drop-down
        // selection, lists the facilities in lexicographical order
        facilityDepartmentsList.sort(new FacilityDepartmentsComparator());
        return facilityDepartmentsList;
    }
}
