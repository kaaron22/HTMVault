package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManufacturerModelConverter implements DynamoDBTypeConverter<String, ManufacturerModel> {

    private static final Gson GSON = new Gson();
    private final Logger log = LogManager.getLogger();

    @Override
    public String convert(ManufacturerModel manufacturerModelToSerialize) {
        return GSON.toJson(manufacturerModelToSerialize);
    }

    @Override
    public ManufacturerModel unconvert(String serializedManufacturerModel) {
        return GSON.fromJson(serializedManufacturerModel, ManufacturerModel.class);
    }
}
