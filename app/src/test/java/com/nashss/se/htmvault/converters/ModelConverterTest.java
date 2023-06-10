package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;

import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        assertEquals(0, deviceModel.getMaintenanceFrequencyInMonths());
        assertEquals(inventoryAddDate.toString(), deviceModel.getInventoryAddDate());
        assertEquals(addedById, deviceModel.getAddedById());
        assertEquals(addedByName, deviceModel.getAddedByName());
        assertEquals("", deviceModel.getNotes());
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
}
