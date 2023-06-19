package com.nashss.se.htmvault.models;

import java.util.List;
import java.util.Objects;

public class FacilityDepartments {

    private final String facility;
    private final List<String> departments;

    private FacilityDepartments(String facility, List<String> departments) {
        this.facility = facility;
        this.departments = departments;
    }

    public String getFacility() {
        return facility;
    }

    public List<String> getDepartments() {
        return departments;
    }

    @Override
    public String toString() {
        return "FacilityDepartments{" +
                "facility='" + facility + '\'' +
                ", departments=" + departments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FacilityDepartments that = (FacilityDepartments) o;
        return Objects.equals(facility, that.facility) && Objects.equals(departments, that.departments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facility, departments);
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String facility;
        private List<String> departments;

        public Builder withFacility(String facility) {
            this.facility = facility;
            return this;
        }

        public Builder withDepartments(List<String> departments) {
            this.departments = departments;
            return this;
        }

        public FacilityDepartments build() {
            return new FacilityDepartments(facility, departments);
        }
    }
}
