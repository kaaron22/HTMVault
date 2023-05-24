package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;

public class ManufacturerModelConverter implements DynamoDBTypeConverter<String, ManufacturerModel> {
    @Override
    public String convert(ManufacturerModel object) {
        return null;
    }

    @Override
    public ManufacturerModel unconvert(String object) {
        return null;
    }
}
