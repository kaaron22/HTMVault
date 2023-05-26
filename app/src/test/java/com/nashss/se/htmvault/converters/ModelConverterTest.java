package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelConverterTest {

    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private ManufacturerModel manufacturerModel;
    private LocalDate manufactureDate;
    private ServiceStatus serviceStatus;
    private String facilityName;
    private String assignedDepartment;
    private LocalDate complianceThroughDate;
    private LocalDate lastPmCompletionDate;
    private LocalDate nextPmDueDate;
    private Integer maintenanceFrequencyInMonths;
    private LocalDate inventoryAddDate;
    private String addedById;
    private String addedByName;
    private String notes;
    private WorkOrderSummary workOrderSummary1;
    private WorkOrderSummary workOrderSummary2;
    private WorkOrderSummary workOrderSummary3;
    private List<WorkOrderSummary> workOrders;

    @BeforeEach
    void setUp() {
        controlNumber = "123";
        serialNumber = "T-456";
        manufacturer = "Test Manufacturer";
        model = "Test Model";

        manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);

        manufactureDate = LocalDate.of(2021, 10, 26);
        serviceStatus = ServiceStatus.IN_SERVICE;
        facilityName = "General Hospital";
        assignedDepartment = "ER";
        complianceThroughDate = LocalDate.of(2023, 12, 31);
        lastPmCompletionDate = LocalDate.of(2022, 12, 8);
        nextPmDueDate = LocalDate.of(2023, 12, 31);
        maintenanceFrequencyInMonths = 12;
        inventoryAddDate = LocalDate.of(2021, 12, 3);
        addedById = "E1234";
        addedByName = "Jane Doe";
        notes = "Storage B when not in use";

        workOrderSummary1 = new WorkOrderSummary();
        workOrderSummary1.setWorkOrderId("1");
        workOrderSummary1.setWorkOrderType(WorkOrderType.ACCEPTANCE_TESTING);
        workOrderSummary1.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary1.setDateTimeCreated(LocalDateTime.parse("2021-12-03T10:15:30"));
        workOrderSummary1.setCompletionDateTime(LocalDateTime.parse("2021-12-03T11:15:30"));

        workOrderSummary2 = new WorkOrderSummary();
        workOrderSummary2.setWorkOrderId("2");
        workOrderSummary2.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrderSummary2.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary2.setDateTimeCreated(LocalDateTime.parse("2022-12-08T10:15:30"));
        workOrderSummary2.setCompletionDateTime(LocalDateTime.parse("2022-12-08T12:15:30"));

        workOrderSummary3 = new WorkOrderSummary();
        workOrderSummary3.setWorkOrderId("7");
        workOrderSummary3.setWorkOrderType(WorkOrderType.REPAIR);
        workOrderSummary3.setCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrderSummary3.setDateTimeCreated(LocalDateTime.parse("2023-3-08T13:22:10"));
        workOrderSummary3.setCompletionDateTime(null);

        workOrders = new ArrayList<>(Arrays.asList(workOrderSummary1, workOrderSummary2, workOrderSummary3));

        Device device = new Device();
        device.setControlNumber(controlNumber);
        device.setSerialNumber(serialNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(manufactureDate);
        device.setServiceStatus(serviceStatus);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setComplianceThroughDate(complianceThroughDate);
        device.setLastPmCompletionDate(lastPmCompletionDate);
        device.setNextPmDueDate(nextPmDueDate);
        device.setMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);
        device.setInventoryAddDate(inventoryAddDate);
        device.setAddedById(addedById);
        device.setAddedByName(addedByName);
        device.setNotes(notes);
        device.setWorkOrders(workOrders);
    }

    @Test
    void toDeviceModel_withNoWorkOrders() {
        // GIVEN
        // setup

        // WHEN

    }
}