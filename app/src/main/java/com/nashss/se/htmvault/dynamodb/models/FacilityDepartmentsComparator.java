package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.FacilityDepartments;

import java.util.Comparator;

/**
 * Compares facility/department objects by the facility name (hash key).
 */
public class FacilityDepartmentsComparator implements Comparator<FacilityDepartments> {
    @Override
    public int compare(FacilityDepartments o1, FacilityDepartments o2) {
        return o1.getFacility().compareTo(o2.getFacility());
    }
}
