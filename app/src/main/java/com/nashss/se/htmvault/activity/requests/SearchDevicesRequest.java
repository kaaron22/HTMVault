package com.nashss.se.htmvault.activity.requests;

public class SearchDevicesRequest {

    private final String criteria;

    private SearchDevicesRequest(String criteria) {
        this.criteria = criteria;
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return "SearchDevicesRequest{" +
                "criteria='" + criteria + '\'' +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String criteria;

        public Builder withCriteria(String criteria) {
            this.criteria = criteria;
            return this;
        }

        public SearchDevicesRequest build() {
            return new SearchDevicesRequest(criteria);
        }
    }
}
