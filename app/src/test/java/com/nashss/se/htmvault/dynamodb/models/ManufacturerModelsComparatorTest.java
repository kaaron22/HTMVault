package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.ManufacturerModels;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManufacturerModelsComparatorTest {

    @Test
    public void compare_unequalManufacturerNames_returnsCorrectResult() {
        // GIVEN
        ManufacturerModels manufacturerModels1 = ManufacturerModels.builder()
                .withManufacturer("Monitor Co.")
                .withModels(new ArrayList<>(Arrays.asList("Their First Monitor Model", "Their Second Monitor Model")))
                .build();
        ManufacturerModels manufacturerModels2 = ManufacturerModels.builder()
                .withManufacturer("Defibrillator Co.")
                .withModels(new ArrayList<>(List.of("Their Only Defibrillator Model")))
                .build();

        // WHEN
        int comparison = new ManufacturerModelsComparator().compare(manufacturerModels1, manufacturerModels2);

        // THEN
        assertTrue(comparison > 0, String.format("Expected manufacturerModel with manufacturer name %s to be " +
                "greater than manufacturerModel with manufacturer name %s", manufacturerModels1.getManufacturer(),
                manufacturerModels2.getManufacturer()));
    }

    @Test
    public void compare_equalManufacturerNames_returnsCorrectResult() {
        // GIVEN
        ManufacturerModels manufacturerModels1 = ManufacturerModels.builder()
                .withManufacturer("Monitor-Defib United")
                .withModels(new ArrayList<>(Arrays.asList("Their First Monitor Model", "Their Second Monitor Model")))
                .build();
        ManufacturerModels manufacturerModels2 = ManufacturerModels.builder()
                .withManufacturer("Monitor-Defib United")
                .withModels(new ArrayList<>(List.of("Their Only Defibrillator Model")))
                .build();

        // WHEN
        int comparison = new ManufacturerModelsComparator().compare(manufacturerModels1, manufacturerModels2);

        // THEN
        assertEquals(0, comparison, String.format("Expected manufacturerModel with manufacturer name %s to " +
                        "be equal to manufacturerModel with manufacturer name %s",
                manufacturerModels1.getManufacturer(), manufacturerModels2.getManufacturer()));
    }
}
