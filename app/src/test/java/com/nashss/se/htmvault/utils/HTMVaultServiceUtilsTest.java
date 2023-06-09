package com.nashss.se.htmvault.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.ALPHA_NUMERIC_SPACE_OR_DASH;
import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.formatLocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HTMVaultServiceUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    public void isValidString_validAlphaNumericSpaceOrDashStringProvided_returnsTrue() {
        // GIVEN
        String serialNumber = "a-valid-alphanumeric-string-with-6-dashes and 3 spaces";

        // WHEN
        boolean result = HTMVaultServiceUtils.isValidString(serialNumber, ALPHA_NUMERIC_SPACE_OR_DASH);

        // THEN
        assertTrue(result);
    }

    @Test
    public void isValidString_invalidAlphaNumericSpaceOrDashStringProvided_returnsFalse() {
        // GIVEN
        String serialNumber = "abc 1234-+";

        // WHEN
        boolean result = HTMVaultServiceUtils.isValidString(serialNumber, ALPHA_NUMERIC_SPACE_OR_DASH);

        // THEN
        assertFalse(result);
    }

    @Test
    public void formatLocalDateTime_localDateTimeProvided_returnsExpectedString() {
        // GIVEN
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023, 5, 31),
                LocalTime.of(12, 20, 10));

        // WHEN
        String formattedLocalDateTime = formatLocalDateTime(localDateTime);

        // THEN
        assertEquals("2023-05-31 12:20:10", formattedLocalDateTime);
    }

    @Test
    public void formatLocalDateTime_nullLocalDateTimeProvided_returnsEmptyString() {
        // GIVEN
        LocalDateTime localDateTime = null;

        // WHEN
        String formattedLocalDateTime = formatLocalDateTime(localDateTime);

        // THEN
        assertEquals("", formattedLocalDateTime);
    }

    @Test
    public void generateId_withPrefixAndLength_returnsStringWithPrefixAndAppendedNumericOfExpectedLength() {
        // GIVEN
        String prefix = "WR";
        int length = 5;

        // WHEN
        String result = HTMVaultServiceUtils.generateId(prefix, length);

        // THEN
        assertTrue(result.startsWith(prefix));
        assertEquals(length + prefix.length(), result.length());
    }
}
