package com.nashss.se.htmvault.models;

import java.util.List;
import java.util.Objects;

public class ManufacturerModels {

    private final List<String> models;

    private ManufacturerModels(List<String> models) {
        this.models = models;
    }

    public List<String> getModels() {
        return models;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManufacturerModels that = (ManufacturerModels) o;
        return Objects.equals(models, that.models);
    }

    @Override
    public int hashCode() {
        return Objects.hash(models);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> models;

        public Builder models(List<String> models) {
            this.models = models;
            return this;
        }

        public ManufacturerModels build() {
            return new ManufacturerModels(models);
        }
    }
}
