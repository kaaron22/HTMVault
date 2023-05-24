package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    private final Logger log = LogManager.getLogger();

    @Override
    public String convert(LocalDateTime localDateTimeToSerialize) {
        return localDateTimeToSerialize.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public LocalDateTime unconvert(String serializedLocalDateTime) {
        return LocalDateTime.parse(serializedLocalDateTime);
    }
}
