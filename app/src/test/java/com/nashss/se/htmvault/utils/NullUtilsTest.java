package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;
import static org.junit.jupiter.api.Assertions.*;

class NullUtilsTest {

    @Test
    public void ifNull_mapRequiredRequestParametersProvidedWithNoNullValues_noExceptionThrown() {
        // GIVEN
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("key1", "value1");
        requiredRequestParameterValues.put("key2", "value2");

        // WHEN
        ifNull(requiredRequestParameterValues);

        // THEN
        // no exception thrown
    }

    @Test
    public void ifNull_mapRequiredRequestParametersProvidedWithANullValue_throwsInvalidAttributeValueException() {
        // GIVEN
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("Control Number", "1234");
        requiredRequestParameterValues.put("Serial Number", null);

        // WHEN & THEN
        assertThrows(InvalidAttributeException.class, () ->
                ifNull(requiredRequestParameterValues),
                "Expected null value for a key required attribute to result in an " +
                        "InvalidAttributeValueException thrown");

        try {
            ifNull(requiredRequestParameterValues);
        } catch (InvalidAttributeException e) {
            assertEquals("The Serial Number must be provided", e.getMessage());
        }
    }

    @Test
    void ifNull_nonNullStringProvidedWithSupplierIfNull_returnsOriginalString() {
        // GIVEN
        String testString = "Hello";

        // WHEN
        String result = ifNull(testString, String::new);

        // THEN
        assertEquals(testString, result);
    }

    @Test
    void ifNull_nullStringProvidedWithSupplierIfNull_returnsNewEmptyString() {
        // GIVEN
        String testString = null;

        // WHEN
        String result = ifNull(testString, String::new);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void testIfNull1() {
    }

    @Test
    void ifNotNull() {
    }

    @Test
    void testIfNotNull() {
    }
}