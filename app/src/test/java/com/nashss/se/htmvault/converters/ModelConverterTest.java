package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
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

    private ModelConverter modelConverter = new ModelConverter();

    private Device device;
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

        // an initial completed acceptance work order when the device first arrived and was placed into service
        workOrderSummary1 = new WorkOrderSummary();
        workOrderSummary1.setWorkOrderId("1");
        workOrderSummary1.setWorkOrderType(WorkOrderType.ACCEPTANCE_TESTING);
        workOrderSummary1.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary1.setDateTimeCreated(LocalDateTime.parse("2021-12-03T10:15:30"));
        workOrderSummary1.setCompletionDateTime(LocalDateTime.parse("2021-12-03T11:15:30"));

        // a completed annual preventative maintenance work order
        workOrderSummary2 = new WorkOrderSummary();
        workOrderSummary2.setWorkOrderId("2");
        workOrderSummary2.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrderSummary2.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary2.setDateTimeCreated(LocalDateTime.parse("2022-12-08T10:15:30"));
        workOrderSummary2.setCompletionDateTime(LocalDateTime.parse("2022-12-08T12:15:30"));

        // an open repair work order
        workOrderSummary3 = new WorkOrderSummary();
        workOrderSummary3.setWorkOrderId("7");
        workOrderSummary3.setWorkOrderType(WorkOrderType.REPAIR);
        workOrderSummary3.setCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrderSummary3.setDateTimeCreated(LocalDateTime.parse("2023-05-25T13:22:10"));
        workOrderSummary3.setCompletionDateTime(null);

        workOrders = new ArrayList<>(Arrays.asList(workOrderSummary1, workOrderSummary2, workOrderSummary3));

        device = new Device();
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
    void toDeviceModel_deviceWithAllValuesPopulated_convertsAndReturnsDeviceModelSuccessfully() {
        // GIVEN
        // setup

        // WHEN
        DeviceModel deviceModel = modelConverter.toDeviceModel(device);

        // THEN
        assertEquals(controlNumber, deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals(manufactureDate.toString(), deviceModel.getManufactureDate());
        assertEquals(serviceStatus.toString(), deviceModel.getServiceStatus());
        assertEquals(facilityName, deviceModel.getFacilityName());
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment());
        assertEquals(complianceThroughDate.toString(), deviceModel.getComplianceThroughDate());
        assertEquals(lastPmCompletionDate.toString(), deviceModel.getLastPmCompletionDate());
        assertEquals(nextPmDueDate.toString(), deviceModel.getNextPmDueDate());
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths());
        assertEquals(inventoryAddDate.toString(), deviceModel.getInventoryAddDate());
        assertEquals(addedById, deviceModel.getAddedById());
        assertEquals(addedByName, deviceModel.getAddedByName());
        assertEquals(notes, deviceModel.getNotes());

        // check each String attribute in the List<List<String>> of work order summaries and verify
        // they were converted as expected from the List<WorkOrderSummary> work orders
        verifyListWorkOrderSummaryConversionToListStringList(device.getWorkOrders(),
                deviceModel.getWorkOrderSummaries());
    }

    @Test
    public void toDeviceModel_deviceWithNoWorkOrders_convertsAndReturnsDeviceModelSuccessfully() {
        // GIVEN
        // setup and list of work order summaries empty
        device.setWorkOrders(new ArrayList<>());

        // WHEN
        DeviceModel deviceModel = modelConverter.toDeviceModel(device);

        // THEN
        assertEquals(controlNumber, deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals(manufactureDate.toString(), deviceModel.getManufactureDate());
        assertEquals(serviceStatus.toString(), deviceModel.getServiceStatus());
        assertEquals(facilityName, deviceModel.getFacilityName());
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment());
        assertEquals(complianceThroughDate.toString(), deviceModel.getComplianceThroughDate());
        assertEquals(lastPmCompletionDate.toString(), deviceModel.getLastPmCompletionDate());
        assertEquals(nextPmDueDate.toString(), deviceModel.getNextPmDueDate());
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths());
        assertEquals(inventoryAddDate.toString(), deviceModel.getInventoryAddDate());
        assertEquals(addedById, deviceModel.getAddedById());
        assertEquals(addedByName, deviceModel.getAddedByName());
        assertEquals(notes, deviceModel.getNotes());
        // List<List<String>> workOrderSummaries is empty
        assertTrue(deviceModel.getWorkOrderSummaries().isEmpty());
    }

    @Test
    public void toDeviceModel_nullDeviceValues_convertsAndReturnsDeviceModelSuccessfully() {
        // GIVEN
        // setup and some values null (these values are either optional, such as the manufacture date
        // or notes, or have yet to be populated, such as the compliance through date and last PM
        // completion date for a newly added device that has not yet had an inspection completed)
        device.setManufactureDate(null);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(null);
        device.setNotes(null);

        // WHEN
        DeviceModel deviceModel = modelConverter.toDeviceModel(device);

        // THEN
        assertEquals(controlNumber, deviceModel.getControlNumber());
        assertEquals(serialNumber, deviceModel.getSerialNumber());
        assertEquals(manufacturer, deviceModel.getManufacturer());
        assertEquals(model, deviceModel.getModel());
        assertEquals("", deviceModel.getManufactureDate());
        assertEquals(serviceStatus.toString(), deviceModel.getServiceStatus());
        assertEquals(facilityName, deviceModel.getFacilityName());
        assertEquals(assignedDepartment, deviceModel.getAssignedDepartment());
        assertEquals("", deviceModel.getComplianceThroughDate());
        assertEquals("", deviceModel.getLastPmCompletionDate());
        assertEquals("", deviceModel.getNextPmDueDate());
        assertEquals(maintenanceFrequencyInMonths, deviceModel.getMaintenanceFrequencyInMonths());
        assertEquals(inventoryAddDate.toString(), deviceModel.getInventoryAddDate());
        assertEquals(addedById, deviceModel.getAddedById());
        assertEquals(addedByName, deviceModel.getAddedByName());
        assertEquals("", deviceModel.getNotes());
        verifyListWorkOrderSummaryConversionToListStringList(device.getWorkOrders(),
                deviceModel.getWorkOrderSummaries());
    }

    /**
     * Checks each String attribute in the List<List<String>> of work order summaries and verifies
     * they were converted as expected from the List<WorkOrderSummary> work orders
     * @param workOrders The list of WorkOrderSummary objects
     * @param workOrderSummaries The list of String lists that was converted from each attribute of each work order
     *                          summary
     */
    private void verifyListWorkOrderSummaryConversionToListStringList(List<WorkOrderSummary> workOrders,
                                                                      List<List<String>> workOrderSummaries) {
        for (int i = 0; i < workOrders.size(); i++) {
            assertEquals(workOrders.get(i).getWorkOrderId(), workOrderSummaries.get(i).get(0));
            assertEquals(workOrders.get(i).getWorkOrderType().toString(),
                    workOrderSummaries.get(i).get(1));
            assertEquals(workOrders.get(i).getCompletionStatus().toString(),
                    workOrderSummaries.get(i).get(2));
            assertEquals(HTMVaultServiceUtils.formatLocalDateTime(workOrders.get(i).getDateTimeCreated()),
                    workOrderSummaries.get(i).get(3));
            assertEquals(null == workOrders.get(i).getCompletionDateTime() ? "" :
                            HTMVaultServiceUtils.formatLocalDateTime(workOrders.get(i).getCompletionDateTime()),
                    workOrderSummaries.get(i).get(4));
        }
    }

}