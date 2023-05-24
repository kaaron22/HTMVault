package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "manufacturer_models")
public class ManufacturerModel {

    private String manufacturer;
    private String model;

    @DynamoDBHashKey(attributeName = "manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @DynamoDBAttribute(attributeName = "model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManufacturerModel that = (ManufacturerModel) o;
        return Objects.equals(manufacturer, that.manufacturer) && Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manufacturer, model);
    }
}
