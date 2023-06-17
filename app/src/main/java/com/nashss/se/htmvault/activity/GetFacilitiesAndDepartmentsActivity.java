package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetFacilitiesAndDepartmentsRequest;
import com.nashss.se.htmvault.activity.results.GetFacilitiesAndDepartmentsResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.*;

public class GetFacilitiesAndDepartmentsActivity {

    private final FacilityDepartmentDao facilityDepartmentDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public GetFacilitiesAndDepartmentsActivity(FacilityDepartmentDao facilityDepartmentDao,
                                               MetricsPublisher metricsPublisher) {
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetFacilitiesAndDepartmentsResult handleRequest(final GetFacilitiesAndDepartmentsRequest request) {
        List<FacilityDepartment> facilityDepartments = facilityDepartmentDao.getFacilityDepartments();

        Map<String, Set<String>> facilitiesAndDepartments = new HashMap<>();
        for (FacilityDepartment facilityDepartment : facilityDepartments) {
            if (!facilitiesAndDepartments.containsKey(facilityDepartment.getFacilityName())) {
                facilitiesAndDepartments.put(facilityDepartment.getFacilityName(),
                        new HashSet<>(List.of(facilityDepartment.getAssignedDepartment())));
            } else {
                Set<String> departments = facilitiesAndDepartments.get(facilityDepartment.getFacilityName());
                departments.add(facilityDepartment.getAssignedDepartment());
                facilitiesAndDepartments.put(facilityDepartment.getFacilityName(), departments);
            }
        }

        return GetFacilitiesAndDepartmentsResult.builder()
                .withFacilitiesAndDepartments(new ModelConverter().toListFacilityDepartments(facilitiesAndDepartments))
                .build();
    }
}
