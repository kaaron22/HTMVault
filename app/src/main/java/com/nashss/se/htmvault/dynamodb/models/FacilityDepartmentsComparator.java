package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.FacilityDepartments;

import java.util.Comparator;

/**
 * Compares public FacilityDepartments objects, each containing a facility name and a list
 * of associated models, by the facility name.
 */
public class FacilityDepartmentsComparator implements Comparator<FacilityDepartments> {
    @Override
    public int compare(FacilityDepartments o1, FacilityDepartments o2) {
        return o1.getFacility().compareTo(o2.getFacility());
    }
}
