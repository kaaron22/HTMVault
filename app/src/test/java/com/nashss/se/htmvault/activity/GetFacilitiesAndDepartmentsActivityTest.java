package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetFacilitiesAndDepartmentsRequest;
import com.nashss.se.htmvault.activity.results.GetFacilitiesAndDepartmentsResult;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.FacilityDepartments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetFacilitiesAndDepartmentsActivityTest {

    @Mock
    private FacilityDepartmentDao facilityDepartmentDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private GetFacilitiesAndDepartmentsActivity getFacilitiesAndDepartmentsActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        getFacilitiesAndDepartmentsActivity =
                new GetFacilitiesAndDepartmentsActivity(facilityDepartmentDao, metricsPublisher);
    }

    @Test
    public void handleRequest_requestForFacilitiesAndDepartments_returnsSortedListOfFacilitiesAndDepartmentsInResult() {
        // GIVEN
        GetFacilitiesAndDepartmentsRequest request = GetFacilitiesAndDepartmentsRequest.builder()
                .build();

        // our individual facility/department objects to be returned when a mock call to the database is made
        FacilityDepartment facilityDepartment1 = new FacilityDepartment();
        facilityDepartment1.setFacilityName("Test Hospital");
        facilityDepartment1.setAssignedDepartment("ICU");

        FacilityDepartment facilityDepartment2 = new FacilityDepartment();
        facilityDepartment2.setFacilityName("Test Hospital");
        facilityDepartment2.setAssignedDepartment("ER");

        FacilityDepartment facilityDepartment3 = new FacilityDepartment();
        facilityDepartment3.setFacilityName("Test Clinic");
        facilityDepartment3.setAssignedDepartment("Convenient Care");

        FacilityDepartment facilityDepartment4 = new FacilityDepartment();
        facilityDepartment4.setFacilityName("Test Surgical Center");
        facilityDepartment4.setAssignedDepartment("Sterile Processing");

        List<FacilityDepartment> facilityDepartmentList = new ArrayList<>(Arrays.asList(facilityDepartment1,
                facilityDepartment2, facilityDepartment3, facilityDepartment4));

        when(facilityDepartmentDao.getFacilityDepartments()).thenReturn(facilityDepartmentList);

        // WHEN
        GetFacilitiesAndDepartmentsResult result = getFacilitiesAndDepartmentsActivity.handleRequest(request);
        // the expected, sorted result
        FacilityDepartments facilityDepartments1 = FacilityDepartments.builder()
                .withFacility("Test Hospital")
                .withDepartments(new ArrayList<>(Arrays.asList("ER", "ICU")))
                .build();
        FacilityDepartments facilityDepartments2 = FacilityDepartments.builder()
                .withFacility("Test Clinic")
                .withDepartments(new ArrayList<>(List.of("Convenient Care")))
                .build();
        FacilityDepartments facilityDepartments3 = FacilityDepartments.builder()
                .withFacility("Test Surgical Center")
                .withDepartments(new ArrayList<>(List.of("Sterile Processing")))
                .build();
        List<FacilityDepartments> expected = new ArrayList<>(Arrays.asList(facilityDepartments2, facilityDepartments1,
                facilityDepartments3));
        List<FacilityDepartments> results = result.getFacilitiesAndDepartments();

        // THEN
        for (int i = 0; i < results.size(); i++) {
            assertTrue(results.contains(expected.get(i)), "The resulting list of FacilityDepartments " +
                    "converted from a list of individual facility/department objects did not match what was expected," +
                    "in the order expected");
        }
    }

    @Test
    public void handleRequest_nullListOfFacilityDepartments_throwsFacilityDepartmentNotFoundException() {
        // GIVEN
        GetFacilitiesAndDepartmentsRequest request = GetFacilitiesAndDepartmentsRequest.builder()
                .build();
        when(facilityDepartmentDao.getFacilityDepartments()).thenThrow(FacilityDepartmentNotFoundException.class);

        // WHEN & THEN
        assertThrows(FacilityDepartmentNotFoundException.class, () ->
                        getFacilitiesAndDepartmentsActivity.handleRequest(request),
                "Expected a request to get facilities and departments that results in a " +
                        "FacilityDepartmentNotFoundException to propagate");
    }
}
