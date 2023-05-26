package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "facility_departments")
public class FacilityDepartment {

    private String facilityName;
    private String assignedDepartment;

    @DynamoDBHashKey(attributeName = "facilityName")
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @DynamoDBHashKey(attributeName = "assignedDepartment")
    public String getDepartment() {
        return assignedDepartment;
    }

    public void setDepartments(String assignedDepartment) {
        this.assignedDepartment = assignedDepartment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacilityDepartment that = (FacilityDepartment) o;
        return Objects.equals(facilityName, that.facilityName) && Objects.equals(assignedDepartment,
                that.assignedDepartment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityName, assignedDepartment);
    }
}
