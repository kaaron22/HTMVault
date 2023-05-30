package com.nashss.se.htmvault.models;


import com.nashss.se.htmvault.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeviceModel {

    private final String controlNumber;
    private final String serialNumber;
    private final String manufacturer;
    private final String model;
    private final String manufactureDate;
    private final String serviceStatus;
    private final String facilityName;
    private final String assignedDepartment;
    private final String complianceThroughDate;
    private final String lastPmCompletionDate;
    private final String nextPmDueDate;
    private final int maintenanceFrequencyInMonths;
    private final String inventoryAddDate;
    private final String addedById;
    private final String addedByName;
    private final String notes;
    private final List<List<String>> workOrderSummaries;

    private DeviceModel(String controlNumber, String serialNumber, String manufacturer, String model,
                       String manufactureDate, String serviceStatus, String facilityName, String assignedDepartment,
                       String complianceThroughDate, String lastPmCompletionDate, String nextPmDueDate,
                       int maintenanceFrequencyInMonths, String inventoryAddDate, String addedById, String addedByName,
                       String notes, List<List<String>> workOrderSummaries) {
        this.controlNumber = controlNumber;
        this.serialNumber = serialNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.manufactureDate = manufactureDate;
        this.serviceStatus = serviceStatus;
        this.facilityName = facilityName;
        this.assignedDepartment = assignedDepartment;
        this.complianceThroughDate = complianceThroughDate;
        this.lastPmCompletionDate = lastPmCompletionDate;
        this.nextPmDueDate = nextPmDueDate;
        this.maintenanceFrequencyInMonths = maintenanceFrequencyInMonths;
        this.inventoryAddDate = inventoryAddDate;
        this.addedById = addedById;
        this.addedByName = addedByName;
        this.notes = notes;
        this.workOrderSummaries = workOrderSummaries;
    }

    public String getControlNumber() {
        return controlNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getAssignedDepartment() {
        return assignedDepartment;
    }

    public String getComplianceThroughDate() {
        return complianceThroughDate;
    }

    public String getLastPmCompletionDate() {
        return lastPmCompletionDate;
    }

    public String getNextPmDueDate() {
        return nextPmDueDate;
    }

    public int getMaintenanceFrequencyInMonths() {
        return maintenanceFrequencyInMonths;
    }

    public String getInventoryAddDate() {
        return inventoryAddDate;
    }

    public String getAddedById() {
        return addedById;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public String getNotes() {
        return notes;
    }

    public List<List<String>> getWorkOrderSummaries() {
        List<List<String>> copyWorkOrderSummaries = new ArrayList<>();
        for (List<String> workOrderSummary : workOrderSummaries) {
            copyWorkOrderSummaries.add(CollectionUtils.copyToList(workOrderSummary));
        }
        return copyWorkOrderSummaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceModel that = (DeviceModel) o;
        return maintenanceFrequencyInMonths == that.maintenanceFrequencyInMonths &&
                Objects.equals(controlNumber, that.controlNumber) &&
                Objects.equals(serialNumber, that.serialNumber) &&
                Objects.equals(manufacturer, that.manufacturer) &&
                Objects.equals(model, that.model) &&
                Objects.equals(manufactureDate, that.manufactureDate) &&
                Objects.equals(serviceStatus, that.serviceStatus) &&
                Objects.equals(facilityName, that.facilityName) &&
                Objects.equals(assignedDepartment, that.assignedDepartment) &&
                Objects.equals(complianceThroughDate, that.complianceThroughDate) &&
                Objects.equals(lastPmCompletionDate, that.lastPmCompletionDate) &&
                Objects.equals(nextPmDueDate, that.nextPmDueDate) &&
                Objects.equals(inventoryAddDate, that.inventoryAddDate) &&
                Objects.equals(addedById, that.addedById) &&
                Objects.equals(addedByName, that.addedByName) &&
                Objects.equals(notes, that.notes) &&
                Objects.equals(workOrderSummaries, that.workOrderSummaries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(controlNumber, serialNumber, manufacturer, model, manufactureDate, serviceStatus,
                facilityName, assignedDepartment, complianceThroughDate, lastPmCompletionDate, nextPmDueDate,
                maintenanceFrequencyInMonths, inventoryAddDate, addedById, addedByName, notes, workOrderSummaries);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String controlNumber;
        private String serialNumber;
        private String manufacturer;
        private String model;
        private String manufactureDate;
        private String serviceStatus;
        private String facilityName;
        private String assignedDepartment;
        private String complianceThroughDate;
        private String lastPmCompletionDate;
        private String nextPmDueDate;
        private int maintenanceFrequencyInMonths;
        private String inventoryAddDate;
        private String addedById;
        private String addedByName;
        private String notes;
        private List<List<String>> workOrderSummaries;

        public Builder withControlNumber(String controlNumber) {
            this.controlNumber = controlNumber;
            return this;
        }

        public Builder withSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder withManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withManufactureDate(String manufactureDate) {
            this.manufactureDate = manufactureDate;
            return this;
        }

        public Builder withServiceStatus(String serviceStatus) {
            this.serviceStatus = serviceStatus;
            return this;
        }

        public Builder withFacilityName(String facilityName) {
            this.facilityName = facilityName;
            return this;
        }

        public Builder withAssignedDepartment(String assignedDepartment) {
            this.assignedDepartment = assignedDepartment;
            return this;
        }

        public Builder withComplianceThroughDate(String complianceThroughDate) {
            this.complianceThroughDate = complianceThroughDate;
            return this;
        }

        public Builder withLastPmCompletionDate(String lastPmCompletionDate) {
            this.lastPmCompletionDate = lastPmCompletionDate;
            return this;
        }

        public Builder withNextPmDueDate(String nextPmDueDate) {
            this.nextPmDueDate = nextPmDueDate;
            return this;
        }

        public Builder withMaintenanceFrequencyInMonths(int maintenanceFrequencyInMonths) {
            this.maintenanceFrequencyInMonths = maintenanceFrequencyInMonths;
            return this;
        }

        public Builder withInventoryAddDate(String inventoryAddDate) {
            this.inventoryAddDate = inventoryAddDate;
            return this;
        }

        public Builder withAddedById (String addedById) {
            this.addedById = addedById;
            return this;
        }

        public Builder withAddedByName (String addedByName) {
            this.addedByName = addedByName;
            return this;
        }

        public Builder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder withWorkOrderSummaries(List<List<String>> workOrderSummaries) {
            this.workOrderSummaries = workOrderSummaries;
            return this;
        }

        public DeviceModel build() {
            return new DeviceModel(controlNumber, serialNumber, manufacturer, model, manufactureDate, serviceStatus,
                    facilityName, assignedDepartment, complianceThroughDate, lastPmCompletionDate, nextPmDueDate,
                    maintenanceFrequencyInMonths, inventoryAddDate, addedById, addedByName, notes, workOrderSummaries);
        }
    }
}
