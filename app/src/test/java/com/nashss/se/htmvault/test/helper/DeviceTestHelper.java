package com.nashss.se.htmvault.test.helper;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

// initial from template project, modified/added methods for capstone
public final class DeviceTestHelper {
    private DeviceTestHelper() {
    }

    public static Device generateActiveDevice(int sequenceNumber, ManufacturerModel manufacturerModel,
                                        String facilityName, String assignedDepartment) {
        Device device = new Device();
        device.setControlNumber(HTMVaultServiceUtils.generateId("", 6));
        device.setSerialNumber("SN" + sequenceNumber);
        device.setManufacturerModel(manufacturerModel);
        device.setManufactureDate(0 == HTMVaultServiceUtils.generateRandomIntWithLimit(2) ? null :
                LocalDate.now().minusYears(HTMVaultServiceUtils.generateRandomIntWithLimit(6))
                        .minusDays(HTMVaultServiceUtils.generateRandomIntWithLimit(366)));
        device.setServiceStatus(ServiceStatus.IN_SERVICE);
        device.setFacilityName(facilityName);
        device.setAssignedDepartment(assignedDepartment);
        device.setLastPmCompletionDate(0 == HTMVaultServiceUtils.generateRandomIntWithLimit(2) ? null :
                LocalDate.now().minusYears(HTMVaultServiceUtils.generateRandomIntWithLimit(3))
                        .minusDays(HTMVaultServiceUtils.generateRandomIntWithLimit(366)));
        LocalDate complianceThroughDate =
                calculateComplianceThroughAndNextPmDueDate(manufacturerModel.getRequiredMaintenanceFrequencyInMonths(),
                        device.getLastPmCompletionDate());
        device.setComplianceThroughDate(complianceThroughDate);
        device.setNextPmDueDate(complianceThroughDate);
        device.setInventoryAddDate(LocalDate.now());
        device.setAddedById("An employee ID");
        device.setAddedByName("An employee name");
        device.setNotes("Some notes" + sequenceNumber);

        return device;
    }

    private static LocalDate calculateComplianceThroughAndNextPmDueDate(Integer maintenanceFrequency,
                                                                        LocalDate lastPmCompletionDate) {
        // if the required maintenance frequency is zero/null, this device does not require a PM
        if (null == maintenanceFrequency || 0 == maintenanceFrequency) {
            return null;
        // otherwise, if the device requires a routine preventative maintenance
        } else {
            // if no PM has been completed previously, it's due now
            if (null == lastPmCompletionDate) {
                return LocalDate.now();
            // otherwise it's due "maintenanceFrequency" months after the last PM, by the end of that month
            } else {
                LocalDate nextPmDueDatePlusOneMonth =
                        lastPmCompletionDate.plusMonths(maintenanceFrequency + 1);
                int dayOfMonth = nextPmDueDatePlusOneMonth.getDayOfMonth();
                return nextPmDueDatePlusOneMonth.minusDays(dayOfMonth);
            }
        }
    }

    public static void assertDeviceEqualsDeviceModel(Device device, DeviceModel deviceModel, String message) {
        assertEquals(device.getControlNumber(), deviceModel.getControlNumber(), "expected the control " +
                "number to match");
        assertEquals(device.getSerialNumber(), deviceModel.getSerialNumber(), "expected the serial number " +
                "to match");
        assertEquals(device.getManufacturerModel().getManufacturer(), deviceModel.getManufacturer(),
                "expected the manufacturer to match");
        assertEquals(device.getManufacturerModel().getModel(), deviceModel.getModel(), "expected the model " +
                "to match");
        assertEquals(null == device.getManufactureDate() ? "" : device.getManufactureDate().toString(),
                deviceModel.getManufactureDate(), "expected the manufacture date to match");
        assertEquals(device.getServiceStatus().toString(), deviceModel.getServiceStatus(), "expected the " +
                "service status to match");
        assertEquals(device.getFacilityName(), deviceModel.getFacilityName(), "expected the facility name " +
                "to match");
        assertEquals(device.getAssignedDepartment(), deviceModel.getAssignedDepartment(), "expected the " +
                "assigned department to match");
        assertEquals(null == device.getComplianceThroughDate() ? "" : device.getComplianceThroughDate().toString(),
                deviceModel.getComplianceThroughDate(), "expected the compliance-through-date to match");
        assertEquals(null == device.getLastPmCompletionDate() ? "" : device.getLastPmCompletionDate().toString(),
                deviceModel.getLastPmCompletionDate(), "expected the last pm completion date to match");
        assertEquals(null == device.getNextPmDueDate() ? "" : device.getNextPmDueDate().toString(),
                deviceModel.getNextPmDueDate(), "expected the next pm due date to match");
        assertEquals(null == device.getManufacturerModel()
                .getRequiredMaintenanceFrequencyInMonths() ? 0 : device.getManufacturerModel()
                .getRequiredMaintenanceFrequencyInMonths(), deviceModel.getMaintenanceFrequencyInMonths(),
                "expected the maintenance frequency to match");
        assertEquals(device.getInventoryAddDate().toString(), deviceModel.getInventoryAddDate(), "expected " +
                "the inventory add date to match");
        assertEquals(device.getAddedById(), deviceModel.getAddedById(), "expected the customer " +
                "id that added the device to match");
        assertEquals(device.getAddedByName(), deviceModel.getAddedByName(), "expected the customer name" +
                "that added the device to match");
        assertEquals(null == device.getNotes() ? "" : device.getNotes(), deviceModel.getNotes(), "expected " +
                "the device notes to match");
    }

    public static void assertDeviceEqualsDeviceModel(Device device, DeviceModel deviceModel) {
        String message = String.format("Expected device %s to match device model %s", device,
                deviceModel);
        assertDeviceEqualsDeviceModel(device, deviceModel, message);
    }

    public static void assertDevicesEqualDeviceModels(List<Device> devices, List<DeviceModel> deviceModelList) {
        assertEquals(devices.size(), deviceModelList.size(),
                String.format("Expected list of devices (%s) and list of device models (%s) to match",
                        devices, deviceModelList)); {
        }
        for (int i = 0; i < devices.size(); i++) {
            assertDeviceEqualsDeviceModel(devices.get(i), deviceModelList.get(i),
                    String.format("Expected %dth device (%s) to match corresponding work order model (%s)",
                            i,
                            devices.get(i),
                            deviceModelList.get(i)));
        }
    }

}
