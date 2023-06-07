package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

    private final Logger log = LogManager.getLogger();

    @Override
    public String convert(LocalDate localDateToSerialize) {
        return localDateToSerialize.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public LocalDate unconvert(String serializedLocalDate) {
        return LocalDate.parse(serializedLocalDate);
    }
}

