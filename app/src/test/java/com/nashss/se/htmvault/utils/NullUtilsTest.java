package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.nashss.se.htmvault.utils.NullUtils.ifNotNull;
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
        assertThrows(InvalidAttributeValueException.class, () ->
                ifNull(requiredRequestParameterValues),
                "Expected null value for a key required attribute to result in an " +
                        "InvalidAttributeValueException thrown");

        try {
            ifNull(requiredRequestParameterValues);
        } catch (InvalidAttributeValueException e) {
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
    void ifNull_nonNullStringProvidedWithValIfNull_returnsOriginalString() {
        // GIVEN
        String testString = "Hello";

        // WHEN
        String result = ifNull(testString, "The test string was empty");

        // THEN
        assertEquals(testString, result);
    }

    @Test
    void ifNull_nullStringProvidedWithValIfNull_returnsExpectedValue() {
        // GIVEN
        String testString = null;

        // WHEN
        String result = ifNull(testString, "The test string was empty");

        // THEN
        assertEquals("The test string was empty", result);
    }

    @Test
    void ifNotNull_nonNullStringProvidedWithValIfNull_returnsExpectedValue() {
        // GIVEN
        String testString = "Hello";

        // WHEN
        String result = ifNotNull(testString, testString + " World!");

        // THEN
        assertEquals("Hello World!", result);
    }

    @Test
    void ifNotNull_nullStringProvidedWithValIfNull_returnsExpectedValue() {
        // GIVEN
        String testString = null;

        // WHEN
        String result = ifNull(testString, "The test string was null");

        // THEN
        assertEquals("The test string was null", result);
    }

    @Test
    void ifNotNull_nonNullStringProvidedWithSupplierIfNotNull_returnsExpectedValue() {
        // GIVEN
        String testString = "Hello";

        // WHEN
        String result = ifNotNull(testString, String::new);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void ifNotNull_nullStringProvidedWithSupplierIfNotNull_returnsExpectedValue() {
        // GIVEN
        String testString = null;

        // WHEN
        String result = ifNotNull(testString, String::new);

        // THEN
        assertNull(result);
    }
}