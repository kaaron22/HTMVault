package com.nashss.se.htmvault.converters;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateConverterTest {

    private final LocalDateConverter localDateConverter = new LocalDateConverter();

    @Test
    void convert_localDateToStringIsoDate_returnsExpectedString() {
        // GIVEN
        LocalDate localDate = LocalDate.of(2023, 5, 30);

        // WHEN
        String serializedDate = localDateConverter.convert(localDate);

        // THEN
        assertEquals("2023-05-30", serializedDate);
    }

    @Test
    void unconvert_stringIsoDateToLocalDate_returnsExpectedLocalDate() {
        // GIVEN
        String serializedDate = "2023-05-30";

        // WHEN
        LocalDate localDate = localDateConverter.unconvert(serializedDate);

        // THEN
        assertEquals(LocalDate.of(2023, 5, 30), localDate);
    }

    @Test
    void unconvert_stringDateNonIsoToLocalDate_throwsDateTimeException() {
        // GIVEN
        String nonIsoDate = "05-30-2023";

        // WHEN & THEN
        assertThrows(DateTimeException.class, () ->
                localDateConverter.unconvert(nonIsoDate),
                "Expected attempted parsing of nonIsoDate to result in DateTimeException thrown");
    }
}