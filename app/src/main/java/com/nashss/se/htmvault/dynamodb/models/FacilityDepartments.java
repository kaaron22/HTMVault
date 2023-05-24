package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.List;
import java.util.Objects;

@DynamoDBTable(tableName = "facility_departments")
public class FacilityDepartments {

    private String facilityId;
    private String facilityName;
    private List<String> departments;

    @DynamoDBHashKey(attributeName = "facilityId")
    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    @DynamoDBAttribute(attributeName = "facilityName")
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    @DynamoDBAttribute(attributeName = "departments")
    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FacilityDepartments that = (FacilityDepartments) o;
        return Objects.equals(facilityId, that.facilityId) && Objects.equals(facilityName, that.facilityName) && Objects.equals(departments, that.departments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityId, facilityName, departments);
    }
}
