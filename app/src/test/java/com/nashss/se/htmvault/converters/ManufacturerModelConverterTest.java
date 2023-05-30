package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManufacturerModelConverterTest {
    private final ManufacturerModelConverter manufacturerModelConverter = new ManufacturerModelConverter();

    @Test
    public void convert_manufacturerModelToJson_returnsStringExpected() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");

        // WHEN
        String manufacturerModelJson = manufacturerModelConverter.convert(manufacturerModel);
        String expectedJson = "{\"manufacturer\":\"TestManufacturer\",\"model\":\"TestModel\"}";

        // THEN
        assertEquals(expectedJson, manufacturerModelJson);
    }
}