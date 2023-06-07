package com.nashss.se.htmvault.test.helper;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.models.ServiceStatus;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.time.LocalDate;

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
                return nextPmDueDatePlusOneMonth.minusDays(dayOfMonth + 1);
            }
        }
    }

}