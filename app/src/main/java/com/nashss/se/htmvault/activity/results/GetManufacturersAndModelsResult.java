package com.nashss.se.htmvault.activity.results;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GetManufacturersAndModelsResult {

    private final Map<String, Set<String>> manufacturersAndModels;

    private GetManufacturersAndModelsResult(Map<String, Set<String>> manufacturersAndModels) {
        this.manufacturersAndModels = manufacturersAndModels;
    }

    public Map<String, Set<String>> getManufacturersAndModels() {
        return new HashMap<>(manufacturersAndModels);
    }

    @Override
    public String toString() {
        return "GetManufacturersAndModelsResult{" +
                "manufacturersAndModels=" + manufacturersAndModels +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Set<String>> manufacturersAndModels;

        public Builder withManufacturersAndModels(Map<String, Set<String>> manufacturersAndModels) {
            this.manufacturersAndModels = new HashMap<>(manufacturersAndModels);
            return this;
        }

        public GetManufacturersAndModelsResult build() {
            return new GetManufacturersAndModelsResult(manufacturersAndModels);
        }
    }
}
