package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.FacilityDepartments;
import com.nashss.se.htmvault.models.ManufacturerModels;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;

import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelConverterTest {

    private final ModelConverter modelConverter = new ModelConverter();

    private Device device;
    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
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

    @BeforeEach
    void setUp() {
        controlNumber = "123";
        serialNumber = "T-456";
        manufacturer = "Test Manufacturer";
        model = "Test Model";
        maintenanceFrequencyInMonths = 12;

        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths);

        manufactureDate = LocalDate.of(2021, 10, 26);
        serviceStatus = ServiceStatus.IN_SERVICE;
        facilityName = "General Hospital";
        assignedDepartment = "ER";
        complianceThroughDate = LocalDate.of(2023, 12, 31);
        lastPmCompletionDate = LocalDate.of(2022, 12, 8);
        nextPmDueDate = LocalDate.of(2023, 12, 31);
        inventoryAddDate = LocalDate.of(2021, 12, 3);
        addedById = "E1234";
        addedByName = "Jane Doe";
        notes = "Storage B when not in use";

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
        device.setInventoryAddDate(inventoryAddDate);
        device.setAddedById(addedById);
        device.setAddedByName(addedByName);
        device.setNotes(notes);
    }

    @Test
    public void toDeviceModel_deviceWithAllValuesPopulated_convertsAndReturnsDeviceModelSuccessfully() {
        // GIVEN
        // setup

        // WHEN
        DeviceModel deviceModel = modelConverter.toDeviceModel(device);

        // THEN
        DeviceTestHelper.assertDeviceEqualsDeviceModel(device, deviceModel);
    }

    @Test
    public void toDeviceModel_nullDeviceValues_convertsAndReturnsDeviceModelSuccessfully() {
        // GIVEN
        // setup and some values null (these values are either optional, such as the manufacture date
        // or notes, or have yet to be populated, such as the compliance through date and last PM
        // completion date for a newly added device, that has not yet had routine maintenance completed or
        // may not ultimately require it).
        // additionally, the device's ManufacturerModel may not have an associated maintenance frequency,
        // which for our purposes is the equivalent of a model having no preventative maintenance
        // requirements (requiredMaintenanceFrequency = 0)
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(null);
        device.setComplianceThroughDate(null);
        device.setLastPmCompletionDate(null);
        device.setNextPmDueDate(null);
        device.setNotes(null);

        // WHEN
        DeviceModel deviceModel = modelConverter.toDeviceModel(device);

        // THEN
        DeviceTestHelper.assertDeviceEqualsDeviceModel(device, deviceModel);
    }

    @Test
    public void toDeviceModelList_withListDevices_convertsToListDeviceModels() {
        // GIVEN
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            devices.add(DeviceTestHelper.generateActiveDevice(i, device.getManufacturerModel(), facilityName,
                    assignedDepartment));
        }

        // WHEN
        List<DeviceModel> deviceModelList = new ModelConverter().toDeviceModelList(devices);

        // THEN
        DeviceTestHelper.assertDevicesEqualDeviceModels(devices, deviceModelList);
    }

    @Test
    public void toWorkOrderModel_withWorkOrder_convertsToWorkOrderModel() {
        // GIVEN
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1,
                HTMVaultServiceUtils.generateId("", 6), "a serial number",
                device.getManufacturerModel(), facilityName, assignedDepartment);

        // WHEN
        WorkOrderModel workOrderModel = modelConverter.toWorkOrderModel(workOrder);

        // THEN
        WorkOrderTestHelper.assertWorkOrderEqualsWorkOrderModel(workOrder, workOrderModel);
    }

    @Test
    public void toWorkOrderModels_withListWorkOrders_convertsToListWorkOrderModels() {
        // GIVEN
        List<WorkOrder> workOrders = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            workOrders.add(WorkOrderTestHelper.generateWorkOrder(1,
                    HTMVaultServiceUtils.generateId("", 6), "a serial number",
                    device.getManufacturerModel(), facilityName, assignedDepartment));
        }

        // WHEN
        List<WorkOrderModel> workOrderModels = modelConverter.toWorkOrderModels(workOrders);

        // THEN
        WorkOrderTestHelper.assertWorkOrdersEqualWorkOrderModels(workOrders, workOrderModels);
    }

    @Test
    public void toListManufacturerModels_mapOfManufacturersToTheirModels_returnsConvertedAndSortedList() {
        // GIVEN
        Map<String, Set<String>> manufacturersAndModels = new HashMap<>();
        manufacturersAndModels.put("Monitor Co.", new HashSet<>(Arrays.asList("Their First Monitor Model",
                "Their Second Monitor Model")));
        manufacturersAndModels.put("Defibrillator Co.", new HashSet<>(List.of("Their Only Defibrillator Model")));
        manufacturersAndModels.put("A Different Monitor Co.",
                new HashSet<>(List.of("Their First Monitor Model So Far")));

        // WHEN
        List<ManufacturerModels> manufacturerAndModelsList =
                modelConverter.toListManufacturerModels(manufacturersAndModels);

        ManufacturerModels manufacturerModels1 = ManufacturerModels.builder()
                .withManufacturer("Monitor Co.")
                .withModels(new ArrayList<>(Arrays.asList("Their First Monitor Model", "Their Second Monitor Model")))
                .build();
        ManufacturerModels manufacturerModels2 = ManufacturerModels.builder()
                .withManufacturer("Defibrillator Co.")
                .withModels(new ArrayList<>(List.of("Their Only Defibrillator Model")))
                .build();
        ManufacturerModels manufacturerModels3 = ManufacturerModels.builder()
                .withManufacturer("A Different Monitor Co.")
                .withModels(new ArrayList<>(List.of("Their First Monitor Model So Far")))
                .build();

        List<ManufacturerModels> expected = new ArrayList<>(Arrays.asList(manufacturerModels3, manufacturerModels2,
                manufacturerModels1));

        // THEN
        assertEquals(expected, manufacturerAndModelsList, String.format("Expected result of converting to " +
                "be %s, but it was %s", expected, manufacturerAndModelsList));
    }

    @Test
    public void toListFacilityDepartments_mapOfFacilitiesToTheirDepartments_returnsConvertedAndSortedList() {
        // GIVEN
        Map<String, Set<String>> facilitiesAndDepartments = new HashMap<>();
        facilitiesAndDepartments.put("Test Hospital", new HashSet<>(Arrays.asList("ICU", "ER", "OR", "NICU")));
        facilitiesAndDepartments.put("Test Clinic", new HashSet<>(List.of("Pediatric Services")));
        facilitiesAndDepartments.put("Test Surgical Center",
                new HashSet<>(Arrays.asList("Endoscopy", "Sterile Processing", "Recovery", "Surgical Services")));

        // WHEN
        List<FacilityDepartments> facilityAndDepartmentsList =
                modelConverter.toListFacilityDepartments(facilitiesAndDepartments);

        FacilityDepartments facilityDepartments1 = FacilityDepartments.builder()
                .withFacility("Test Hospital")
                .withDepartments(new ArrayList<>(Arrays.asList("ER", "ICU", "NICU", "OR")))
                .build();
        FacilityDepartments facilityDepartments2 = FacilityDepartments.builder()
                .withFacility("Test Clinic")
                .withDepartments(new ArrayList<>(List.of("Pediatric Services")))
                .build();
        FacilityDepartments facilityDepartments3 = FacilityDepartments.builder()
                .withFacility("Test Surgical Center")
                .withDepartments(new ArrayList<>(List.of("Endoscopy", "Recovery", "Sterile Processing",
                        "Surgical Services")))
                .build();

        List<FacilityDepartments> expected = new ArrayList<>(Arrays.asList(facilityDepartments2, facilityDepartments1,
                facilityDepartments3));

        // THEN
        assertEquals(expected, facilityAndDepartmentsList, String.format("Expected result of converting to " +
                "be %s, but it was %s", expected, facilityAndDepartmentsList));
    }
}
