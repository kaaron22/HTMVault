package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    private static final Gson GSON = new Gson();
    private final Logger log = LogManager.getLogger();


    @Override
    public String convert(LocalDateTime localDateTimeToSerialize) {
        return GSON.toJson(localDateTimeToSerialize);
    }

    @Override
    public LocalDateTime unconvert(String serializedLocalDateTime) {
        return GSON.fromJson(serializedLocalDateTime, LocalDateTime.class);
    }
}
