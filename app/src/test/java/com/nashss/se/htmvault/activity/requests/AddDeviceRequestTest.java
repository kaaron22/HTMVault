package com.nashss.se.htmvault.activity.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddDeviceRequestTest {

    private static final String ASSERT_EQUALS_FAILURE_MESSAGE = "Expected value set in request object for the %s to " +
            "be what was passed to builder";
    private String serialNumber;
    private String manufacturer;
    private String model;
    private String manufactureDate;
    private String facilityName;
    private String assignedDepartment;
    private String notes;
    private String customerId;
    private String customerName;

    @BeforeEach
    void setUp() {
        serialNumber = "456";
        manufacturer = "a manufacturer";
        model = "a model";
        manufactureDate = "2023-05-26";
        facilityName = "a hospital";
        assignedDepartment = "ER";
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
        // all values should be set in the request object and should match the parameter passed in
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "serialNumber"));
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "manufacturer"));
        assertEquals(model, addDeviceRequest.getModel(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "model"));
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "manufacture date"));
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "facility name"));
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "assigned department"));
        assertEquals(notes, addDeviceRequest.getNotes(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "notes"));
        assertEquals(customerId, addDeviceRequest.getCustomerId(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer id"));
        assertEquals(customerName, addDeviceRequest.getCustomerName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer name"));
    }

    @Test
    public void builder_withValuesForSomeFields_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // manufacture date and notes not included in build; all others included
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        // those values should be null; the ones that were set should match the value passed in
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "serialNumber"));
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "manufacturer"));
        assertEquals(model, addDeviceRequest.getModel(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "model"));
        assertNull(addDeviceRequest.getManufactureDate(), "Expected value for manufacture date not built to " +
                "be null");
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "facility name"));
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "assigned department"));
        assertNull(addDeviceRequest.getNotes(), "Expected value for notes not built to be null");
        assertEquals(customerId, addDeviceRequest.getCustomerId(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer id"));
        assertEquals(customerName, addDeviceRequest.getCustomerName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer name"));
    }

    @Test
    public void builder_maintenanceFrequencyNotWithBuild_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // maintenanceFrequencyInMonths not included in build, all others included
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
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
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "serialNumber"));
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "manufacturer"));
        assertEquals(model, addDeviceRequest.getModel(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "model"));
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "manufacture date"));
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "facility name"));
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "assigned department"));
        assertEquals(notes, addDeviceRequest.getNotes(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "notes"));
        assertEquals(customerId, addDeviceRequest.getCustomerId(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer id"));
        assertEquals(customerName, addDeviceRequest.getCustomerName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer name"));
    }

    @Test
    public void builder_nullPassedInForOneValueToBuilder_returnsAddDeviceRequestProperlyBuilt() {
        // GIVEN
        // setup values

        // WHEN
        // all values passed in, except null for notes
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withManufactureDate(manufactureDate)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withNotes(null)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        // THEN
        assertEquals(serialNumber, addDeviceRequest.getSerialNumber(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "serialNumber"));
        assertEquals(manufacturer, addDeviceRequest.getManufacturer(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "manufacturer"));
        assertEquals(model, addDeviceRequest.getModel(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "model"));
        assertEquals(manufactureDate, addDeviceRequest.getManufactureDate(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "manufacture date"));
        assertEquals(facilityName, addDeviceRequest.getFacilityName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "facility name"));
        assertEquals(assignedDepartment, addDeviceRequest.getAssignedDepartment(),
                String.format(ASSERT_EQUALS_FAILURE_MESSAGE, "assigned department"));
        assertNull(addDeviceRequest.getNotes(), "Expected value for notes not built to be null");
        assertEquals(customerId, addDeviceRequest.getCustomerId(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer id"));
        assertEquals(customerName, addDeviceRequest.getCustomerName(), String.format(ASSERT_EQUALS_FAILURE_MESSAGE,
                "customer name"));
    }

    @Test
    public void toString_withValuesForAllFields_returnsExpectedString() {
        // GIVEN
        // setup values

        // WHEN
        // all values included in build
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
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

        String expectedString = "AddDeviceRequest{" +
                ", serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + manufactureDate + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", notes='" + notes + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';

        // THEN
        assertEquals(expectedString, addDeviceRequest.toString(), "the 'toString' method did not match what " +
                "was expected with all values non-null");
    }

    @Test
    public void toString_withValuesForSomeFields_returnsExpectedString() {
        // GIVEN
        // setup values

        // WHEN
        // manufacture date and notes not included in build, all others included
        AddDeviceRequest addDeviceRequest = AddDeviceRequest.builder()
                .withSerialNumber(serialNumber)
                .withManufacturer(manufacturer)
                .withModel(model)
                .withFacilityName(facilityName)
                .withAssignedDepartment(assignedDepartment)
                .withCustomerId(customerId)
                .withCustomerName(customerName)
                .build();

        String expectedString = "AddDeviceRequest{" +
                ", serialNumber='" + serialNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", manufactureDate='" + "null" + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", assignedDepartment='" + assignedDepartment + '\'' +
                ", notes='" + "null" + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                '}';

        // THEN
        // those values should be "null" in the string; the ones that were built should match the value passed in
        assertEquals(expectedString, addDeviceRequest.toString(), "the 'toString' method did not match what " +
                "was expected with some values null");
    }
}
