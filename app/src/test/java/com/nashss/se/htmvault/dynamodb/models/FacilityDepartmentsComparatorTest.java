package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.FacilityDepartments;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FacilityDepartmentsComparatorTest {

    @Test
    public void compare_unequalFacilityNames_returnsCorrectResult() {
        // GIVEN
        FacilityDepartments facilityDepartments1 = FacilityDepartments.builder()
                .withFacility("Test Hospital")
                .withDepartments(new ArrayList<>(Arrays.asList("ER", "NICU", "ICU", "OR")))
                .build();
        FacilityDepartments facilityDepartments2 = FacilityDepartments.builder()
                .withFacility("Test Clinic")
                .withDepartments(new ArrayList<>(Arrays.asList("Family Practice", "Convenient Care", "Lab")))
                .build();

        // WHEN
        int comparison = new FacilityDepartmentsComparator().compare(facilityDepartments1, facilityDepartments2);

        // THEN
        assertTrue(comparison > 0, String.format("Expected facilityDepartment with facility name %s to be " +
                        "greater than facilityDepartment with facility name %s", facilityDepartments1.getFacility(),
                facilityDepartments2.getFacility()));
    }

    @Test
    public void compare_equalManufacturerNames_returnsCorrectResult() {
        // GIVEN
        FacilityDepartments facilityDepartments1 = FacilityDepartments.builder()
                .withFacility("Test Hospital")
                .withDepartments(new ArrayList<>(Arrays.asList("OR", "NICU", "ER")))
                .build();
        FacilityDepartments facilityDepartments2 = FacilityDepartments.builder()
                .withFacility("Test Hospital")
                .withDepartments(new ArrayList<>(List.of("PACU")))
                .build();

        // WHEN
        int comparison = new FacilityDepartmentsComparator().compare(facilityDepartments1, facilityDepartments2);

        // THEN
        assertEquals(0, comparison, String.format("Expected facilityDepartment with facility name %s to " +
                        "be equal to facilityDepartment with facility name %s",
                facilityDepartments1.getFacility(), facilityDepartments2.getFacility()));
    }

}