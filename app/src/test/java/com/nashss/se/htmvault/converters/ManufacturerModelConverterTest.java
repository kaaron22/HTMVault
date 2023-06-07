package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ManufacturerModelConverterTest {
    private final ManufacturerModelConverter manufacturerModelConverter = new ManufacturerModelConverter();

    @Test
    public void convert_manufacturerModelToJson_returnsStringExpected() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        // WHEN
        String manufacturerModelJson = manufacturerModelConverter.convert(manufacturerModel);
        String expectedJson = "{\"manufacturer\":\"TestManufacturer\",\"model\":\"TestModel\"," +
                "\"requiredMaintenanceFrequencyInMonths\":6}";

        // THEN
        assertEquals(expectedJson, manufacturerModelJson);
    }

    @Test
    public void unconvert_manufacturerModelJsonToManufacturerModel_returnsManufacturerModelExpected() {
        // GIVEN
        String manufacturerModelJson = "{\"manufacturer\":\"TestManufacturer\",\"model\":\"TestModel\"," +
                "\"requiredMaintenanceFrequencyInMonths\":6}";

        // WHEN
        ManufacturerModel manufacturerModel = manufacturerModelConverter.unconvert(manufacturerModelJson);

        // THEN
        assertEquals("TestManufacturer", manufacturerModel.getManufacturer());
        assertEquals("TestModel", manufacturerModel.getModel());
        assertEquals(6, manufacturerModel.getRequiredMaintenanceFrequencyInMonths());
    }

    @Test
    public void unconvert_manufacturerValueNotIncluded_throwsJsonSyntaxException() {
        // GIVEN
        String manufacturerModelJsonMissingManufacturerValue =
                "{\"TestManufacturer\",\"model\":\"TestModel\"}";

        // WHEN & THEN
        assertThrows(JsonSyntaxException.class, () ->
                manufacturerModelConverter.unconvert(manufacturerModelJsonMissingManufacturerValue),
                "Expected attempt to deserialize ManufacturerModel without manufacturer to " +
                        "result in JsonSyntaxException thrown");
    }
}
