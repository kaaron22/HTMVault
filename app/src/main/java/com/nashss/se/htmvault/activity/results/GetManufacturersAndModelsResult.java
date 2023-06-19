package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.ManufacturerModels;

import java.util.*;

public class GetManufacturersAndModelsResult {

    private final List<ManufacturerModels> manufacturersAndModels;

    private GetManufacturersAndModelsResult(List<ManufacturerModels> manufacturersAndModels) {
        this.manufacturersAndModels = manufacturersAndModels;
    }

    public List<ManufacturerModels> getManufacturersAndModels() {
        return new ArrayList<>(manufacturersAndModels);
    }

    @Override
    public String toString() {
        return "GetManufacturersAndModelsResult{" +
                "manufacturersAndModels=" + manufacturersAndModels +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ManufacturerModels> manufacturersAndModels;

        public Builder withManufacturersAndModels(List<ManufacturerModels> manufacturersAndModels) {
            this.manufacturersAndModels = new ArrayList<>(manufacturersAndModels);
            return this;
        }

        public GetManufacturersAndModelsResult build() {
            return new GetManufacturersAndModelsResult(manufacturersAndModels);
        }
    }
}
