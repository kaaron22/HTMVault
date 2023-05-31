package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.ifEmptyOrBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HTMVaultServiceUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void ifEmptyOrBlank_noMissingValues_doesNotThrowException() {
        // GIVEN
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("key1", "value1");
        requiredRequestParameterValues.put("key2", "value2");

        // WHEN
        ifEmptyOrBlank(requiredRequestParameterValues);

        // THEN
        // no exception thrown
    }

    @Test
    void ifEmptyOrBlank_emptyValueForAttribute_throwsInvalidAttributeValueException() {
        // GIVEN
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("Control Number", "1234");
        requiredRequestParameterValues.put("Serial Number", "");

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        ifEmptyOrBlank(requiredRequestParameterValues),
                "Expected empty string value for a key required attribute to result in an " +
                        "InvalidAttributeValueException thrown");

        try {
            ifEmptyOrBlank(requiredRequestParameterValues);
        } catch (InvalidAttributeValueException e) {
            assertEquals("The Serial Number must not be empty or blank", e.getMessage());
        }
    }

    @Test
    void ifNotValidString() {
    }

    @Test
    void allowedMaintenanceFrequencyRange() {
    }

    @Test
    void formatLocalDateTime() {
    }
}