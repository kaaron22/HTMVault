package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Objects;

@DynamoDBTable(tableName = "facility_departments")
public class FacilityDepartment {

    private String facilityName;
    private String department;

    @DynamoDBHashKey(attributeName = "facilityName")
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @DynamoDBHashKey(attributeName = "department")
    public String getDepartment() {
        return department;
    }

    public void setDepartments(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacilityDepartment that = (FacilityDepartment) o;
        return Objects.equals(facilityName, that.facilityName) && Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityName, department);
    }
}
