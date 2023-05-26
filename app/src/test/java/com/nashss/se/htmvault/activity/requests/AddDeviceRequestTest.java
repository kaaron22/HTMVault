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
        // all request fields built
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
        // manufacture date and notes not included in build; all others included
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
        assertNull(addDeviceRequest.getManufactureDate(), "Expected value not built to be null");
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(maintenanceFrequencyInMonths, addDeviceRequest.getMaintenanceFrequencyInMonths(),
                ASSERT_EQUALS_FAILURE_MESSAGE);
        assertNull(addDeviceRequest.getNotes(), "Expected value not built to be null");
        assertEquals(customerId, addDeviceRequest.getCustomerId(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerName, addDeviceRequest.getCustomerName(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }

    @Test
    public void builder_maintenanceFrequencyNotWithBuild_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // maintenanceFrequencyInMonths not included in build, all others included
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

    @Test
    public void builder_nullPassedInForOneValueToBuilder_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // all values passed in, except null for notes
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withControlNumber(controlNumber)
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withMaintenanceFrequencyInMonths(maintenanceFrequencyInMonths)
                .withNotes(null)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        assertEquals(controlNumber, addDeviceRequest.getControlNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(model, addDeviceRequest.getModel(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(maintenanceFrequencyInMonths, addDeviceRequest.getMaintenanceFrequencyInMonths(),
                ASSERT_EQUALS_FAILURE_MESSAGE);
        assertNull(addDeviceRequest.getNotes(), "Expected value not built to be null");
        assertEquals(customerId, addDeviceRequest.getCustomerId(), ASSERT_EQUALS_FAILURE_MESSAGE);
        assertEquals(customerName, addDeviceRequest.getCustomerName(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }

    @Test
    public void toString_withValuesForAllFields_returnsExpectedString() {
        // GIVEN
        // setup values

        // WHEN
        // all values included in build
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

        String expectedString = "AddDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + manufactureDate + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", maintenanceFrequencyInMonths=" + maintenanceFrequencyInMonths +
                ", notes='" + notes + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';

        // THEN
        assertEquals(expectedString, addDeviceRequest.toString(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }

    @Test
    public void toString_withValuesForSomeFields_returnsExpectedString() {
        // GIVEN
        // setup values

        // WHEN
        // manufacture date and notes not included in build, all others included
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

        String expectedString = "AddDeviceRequest{" +
                "controlNumber='" + controlNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + "null" + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", maintenanceFrequencyInMonths=" + maintenanceFrequencyInMonths +
                ", notes='" + "null" + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';

        // THEN
        // those values should be "null" in the string; the ones that were built should match the value passed in
        assertEquals(expectedString, addDeviceRequest.toString(), ASSERT_EQUALS_FAILURE_MESSAGE);
    }
}