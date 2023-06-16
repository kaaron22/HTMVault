package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.FacilityDepartments;

import java.util.ArrayList;
import java.util.List;

public class GetFacilitiesAndDepartmentsResult {

    private final List<FacilityDepartments> facilitiesAndDepartments;

    private GetFacilitiesAndDepartmentsResult(List<FacilityDepartments> facilitiesAndDepartments) {
        this.facilitiesAndDepartments = facilitiesAndDepartments;
    }

    public List<FacilityDepartments> getFacilitiesAndDepartments() {
        return new ArrayList<>(facilitiesAndDepartments);
    }

    @Override
    public String toString() {
        return "GetFacilitiesAndDepartmentsResult{" +
                "facilitiesAndDepartments=" + facilitiesAndDepartments +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<FacilityDepartments> facilitiesAndDepartments;

        public Builder withFacilitiesAndDepartments(List<FacilityDepartments> facilitiesAndDepartments) {
            this.facilitiesAndDepartments = new ArrayList<>(facilitiesAndDepartments);
            return this;
        }

        public GetFacilitiesAndDepartmentsResult build() {
            return new GetFacilitiesAndDepartmentsResult(facilitiesAndDepartments);
        }
    }
}
