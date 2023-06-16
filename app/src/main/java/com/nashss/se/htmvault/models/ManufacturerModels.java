package com.nashss.se.htmvault.models;

import java.util.List;
import java.util.Objects;

public class ManufacturerModels {

    private final String manufacturer;
    private final List<String> models;

    private ManufacturerModels(String manufacturer, List<String> models) {
        this.manufacturer = manufacturer;
        this.models = models;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public List<String> getModels() {
        return models;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String manufacturer;
        private List<String> models;

        public Builder withManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withModels(List<String> models) {
            this.models = models;
            return this;
        }

        public ManufacturerModels build() {
            return new ManufacturerModels(manufacturer, models);
        }
    }
}
