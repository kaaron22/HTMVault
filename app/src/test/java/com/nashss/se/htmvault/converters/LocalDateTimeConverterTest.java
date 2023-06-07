package com.nashss.se.htmvault.converters;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateTimeConverterTest {
    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

    @Test
    void convert_localDateTimeToStringIsoDate_returnsExpectedString() {
        // GIVEN
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023, 5, 30),
                LocalTime.of(12, 18, 20));

        // WHEN
        String serializedDateTime = localDateTimeConverter.convert(localDateTime);

        // THEN
        assertEquals("2023-05-30T12:18:20", serializedDateTime);
    }

    @Test
    void unconvert_stringIsoDateToLocalDate_returnsExpectedLocalDate() {
        // GIVEN
        String serializedDate = "2023-05-30T12:18:20";

        // WHEN
        LocalDateTime localDateTime = localDateTimeConverter.unconvert(serializedDate);

        // THEN
        assertEquals(LocalDateTime.of(LocalDate.of(2023, 5, 30),
                LocalTime.of(12, 18, 20)), localDateTime);
    }

    @Test
    void unconvert_stringDateTimeNonIsoToLocalDate_throwsDateTimeException() {
        // GIVEN
        String nonIsoDateTime = "2023-5-30T12:18:20";

        // WHEN & THEN
        assertThrows(DateTimeException.class, () ->
                        localDateTimeConverter.unconvert(nonIsoDateTime),
                "Expected attempted parsing of nonIsoDate to result in DateTimeException thrown");
    }
}
