package com.nashss.se.htmvault.utils;

import org.junit.jupiter.api.Test;

import static com.nashss.se.htmvault.utils.NullUtils.ifNotNull;
import static com.nashss.se.htmvault.utils.NullUtils.ifNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NullUtilsTest {

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
