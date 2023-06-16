package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.ManufacturerModels;

import java.util.Comparator;

public class ManufacturerModelsComparator implements Comparator<ManufacturerModels> {
    @Override
    public int compare(ManufacturerModels o1, ManufacturerModels o2) {
        return o1.getManufacturer().compareTo(o2.getManufacturer());
    }
}