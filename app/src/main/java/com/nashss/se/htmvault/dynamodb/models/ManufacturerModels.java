package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "manufacturer_models")
public class ManufacturerModels {

    private String manufacturer;
    private List<String> models;

    @DynamoDBHashKey(attributeName = "manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @DynamoDBAttribute(attributeName = "models")
    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManufacturerModels that = (ManufacturerModels) o;
        return Objects.equals(manufacturer, that.manufacturer) && Objects.equals(models, that.models);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manufacturer, models);
    }
}
