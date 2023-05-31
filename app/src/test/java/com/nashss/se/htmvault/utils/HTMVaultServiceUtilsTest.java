package com.nashss.se.htmvault.utils;

import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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

        // WHEN & THEN
        assertDoesNotThrow(() -> ifEmptyOrBlank(requiredRequestParameterValues),
                "Expected map of parameters with non-empty/non-blank values to not result in an exception " +
                        "thrown");
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
    void ifEmptyOrBlank_blankValueForAttribute_throwsInvalidAttributeValueException() {
        // GIVEN
        Map<String, String> requiredRequestParameterValues = new HashMap<>();
        requiredRequestParameterValues.put("Control Number", "1234");
        requiredRequestParameterValues.put("Serial Number", "   ");

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
    void ifNotValidString_validStringProvidedForConditions_doesNotThrowException() {
        // GIVEN
        String attributeName = "Control Number";
        String controlNumber = "1234";
        Predicate<Character> characterCanBeAlphanumeric = Character::isLetterOrDigit;

        // WHEN & THEN
        assertDoesNotThrow(() -> ifNotValidString(attributeName, controlNumber,
                new ArrayList<>(List.of(characterCanBeAlphanumeric))),
                "Expected string with all characters meeting one of the conditions to not result in an " +
                        "exception thrown");
    }

    @Test
    void ifNotValidString_invalidStringProvidedForConditions_throwsInvalidAttributeValueException() {
        // GIVEN
        String attributeName = "Control Number";
        String controlNumber = "1234-";
        Predicate<Character> characterCanBeAlphanumeric = Character::isLetterOrDigit;

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                ifNotValidString(attributeName, controlNumber, new ArrayList<>(List.of(characterCanBeAlphanumeric))),
                "Expected control number containing a non-alphanumeric character to result in " +
                        "an InvalidAttributeValueException thrown");

        try {
            ifNotValidString(attributeName, controlNumber, new ArrayList<>(List.of(characterCanBeAlphanumeric)));
        } catch (InvalidAttributeValueException e) {
            assertEquals("The Control Number provided (1234-) contained invalid characters", e.getMessage());
        }
    }

    @Test
    void ifNotValidString_validStringProvidedForMultipleConditions_doesNotThrowException() {
        // GIVEN
        String attributeName = "Serial Number";
        String controlNumber = "1234-";
        Predicate<Character> characterCanBeAlphanumeric = Character::isLetterOrDigit;
        Predicate<Character> characterCanBeADash = c -> c.equals('-');

        // WHEN
        assertDoesNotThrow(() -> ifNotValidString(attributeName, controlNumber,
                new ArrayList<>(List.of(characterCanBeAlphanumeric, characterCanBeADash))),
                "Expected string with all characters meeting one of the conditions to not result in an " +
                        "exception thrown");
    }

    @Test
    void ifNotValidString_invalidStringProvidedForMultipleConditions_throwsInvalidAttributeValueException() {
        // GIVEN
        String attributeName = "Serial Number";
        String controlNumber = "1234-+";
        Predicate<Character> characterCanBeAlphanumeric = Character::isLetterOrDigit;
        Predicate<Character> characterCanBeADash = c -> c.equals('-');

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        ifNotValidString(attributeName, controlNumber,
                                new ArrayList<>(List.of(characterCanBeAlphanumeric, characterCanBeADash))),
                "Expected serial number containing a character that was not either a dash or alphanumeric " +
                        "to result in an InvalidAttributeValueException thrown");

        try {
            ifNotValidString(attributeName, controlNumber,
                    new ArrayList<>(List.of(characterCanBeAlphanumeric, characterCanBeADash)));
        } catch (InvalidAttributeValueException e) {
            assertEquals("The Serial Number provided (1234-+) contained invalid characters", e.getMessage());
        }
    }

    @Test
    void allowedMaintenanceFrequencyRange_actualValueWithinRange_doesNotThrowException() {
        // GIVEN
        int min = 0;
        int max = 10;
        int actual = 5;

        // WHEN & THEN
        assertDoesNotThrow(() -> allowedMaintenanceFrequencyRange(min, max, actual),
                "Expected actual value within acceptable range to not result in an exception thrown");
    }

    @Test
    void allowedMaintenanceFrequencyRange_actualValueAtMax_doesNotThrowException() {
        // GIVEN
        int min = 0;
        int max = 10;
        int actual = 10;

        // WHEN & THEN
        assertDoesNotThrow(() -> allowedMaintenanceFrequencyRange(min, max, actual),
                "Expected actual value within acceptable range to not result in an exception thrown");
    }

    @Test
    void allowedMaintenanceFrequencyRange_actualValueAboveMax_throwsInvalidAttributeValueException() {
        // GIVEN
        int min = 0;
        int max = 10;
        int actual = 11;

        // WHEN & THEN
        assertThrows(InvalidAttributeValueException.class, () ->
                        allowedMaintenanceFrequencyRange(min, max, actual),
                "Expected actual value above max acceptable range to result in an " +
                        "InvalidAttributeValueException thrown");
    }

    @Test
    void formatLocalDateTime_localDateTimeProvided_returnsExpectedString() {
        // GIVEN
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023, 5, 31),
                LocalTime.of(12, 20, 10));

        // WHEN
        String formattedLocalDateTime = formatLocalDateTime(localDateTime);

        // THEN
        assertEquals("2023-05-31 12:20:10", formattedLocalDateTime);
    }

    @Test
    void formatLocalDateTime_nullLocalDateTimeProvided_returnsEmptyString() {
        // GIVEN
        LocalDateTime localDateTime = null;

        // WHEN
        String formattedLocalDateTime = formatLocalDateTime(localDateTime);

        // THEN
        assertEquals("", formattedLocalDateTime);
    }
}