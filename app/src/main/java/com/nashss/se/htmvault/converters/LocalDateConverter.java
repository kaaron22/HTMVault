package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;

public class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

    private static final Gson GSON = new Gson();
    private final Logger log = LogManager.getLogger();


    @Override
    public String convert(LocalDate localDateToSerialize) {
        return GSON.toJson(localDateToSerialize);
    }

    @Override
    public LocalDate unconvert(String serializedLocalDate) {
        return GSON.fromJson(serializedLocalDate, LocalDate.class);
    }
}

