package com.nashss.se.htmvault.activity.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddDeviceRequestTest {

    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String manufactureDate;
    private String facilityName;
    private String assignedDepartment;
    private int maintenanceFrequencyInMonths;
    private String notes;
    private String customerId;
    private String customerName;
    private static final String ASSERT_EQUALS_FAILURE_MESSAGE = "Expected value set in request object to be what was " +
            "passed to builder";

    @BeforeEach
    void setUp() {
        controlNumber = "123";
        serialNumber = "456";
        manufacturer = "a manufacturer";
        model = "a model";
        manufactureDate = "2023-05-26";
        facilityName = "a hospital";
        assignedDepartment = "ER";
        maintenanceFrequencyInMonths = 6;
        notes = "some notes";
        customerId = "227345";
        customerName = "John Doe";
    }

    @Test
    public void builder_withValuesForAllFields_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        // all values should be set in the request object and should match the parameter passed in
        assertEquals(controlNumber, addDeviceRequest.getControlNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(model, addDeviceRequest.getModel(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(maintenanceFrequencyInMonths, addDeviceRequest.getMaintenanceFrequencyInMonths(),
                ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(notes, addDeviceRequest.getNotes(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerId, addDeviceRequest.getCustomerId(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerName, addDeviceRequest.getCustomerName(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }

    @Test
    public void builder_withValuesForSomeFields_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // manufacture date and notes not included in build
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        // those values should be null; the ones that were set should match the value passed in
        assertEquals(controlNumber, addDeviceRequest.getControlNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(model, addDeviceRequest.getModel(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertNull(addDeviceRequest.getManufactureDate(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(maintenanceFrequencyInMonths, addDeviceRequest.getMaintenanceFrequencyInMonths(),
                ASSERT_EQUALS_FAILURE_MESSAGE);
        assertNull(addDeviceRequest.getNotes(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerId, addDeviceRequest.getCustomerId(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerName, addDeviceRequest.getCustomerName(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }

    @Test
    public void builder_maintenanceFrequencyNotWithBuild_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // maintenanceFrequencyInMonths not included in build
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(notes)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        // the maintenance frequency set is 0, the remaining values should be set in the request object and should match
        // the values passed in
        assertEquals(controlNumber, addDeviceRequest.getControlNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(model, addDeviceRequest.getModel(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(0, addDeviceRequest.getMaintenanceFrequencyInMonths(),
                ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(notes, addDeviceRequest.getNotes(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerId, addDeviceRequest.getCustomerId(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerName, addDeviceRequest.getCustomerName(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }


}