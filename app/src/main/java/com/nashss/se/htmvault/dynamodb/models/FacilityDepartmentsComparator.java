package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.FacilityDepartments;

import java.util.Comparator;

public class FacilityDepartmentsComparator implements Comparator<FacilityDepartments> {
    @Override
    public int compare(FacilityDepartments o1, FacilityDepartments o2) {
        return o1.getFacility().compareTo(o2.getFacility());
    }
}
