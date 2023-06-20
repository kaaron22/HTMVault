package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetFacilitiesAndDepartmentsRequest;
import com.nashss.se.htmvault.activity.results.GetFacilitiesAndDepartmentsResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class GetFacilitiesAndDepartmentsActivity {

    private final FacilityDepartmentDao facilityDepartmentDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Get facilities and departments activity.
     *
     * @param facilityDepartmentDao the facility department dao
     * @param metricsPublisher      the metrics publisher
     */
    @Inject
    public GetFacilitiesAndDepartmentsActivity(FacilityDepartmentDao facilityDepartmentDao,
                                               MetricsPublisher metricsPublisher) {
        this.facilityDepartmentDao = facilityDepartmentDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to get a full list of individual facility/department objects from the database table,
     * converting the list to a list of objects that each contain a facility name and a list of the departments
     * associated with the facility.
     *
     * @param request the request
     * @return the get facilities and departments result
     */
    public GetFacilitiesAndDepartmentsResult handleRequest(final GetFacilitiesAndDepartmentsRequest request) {
        log.info("Received GetFacilitiesAndDepartmentsRequest {}", request);

        List<FacilityDepartment> facilityDepartments = facilityDepartmentDao.getFacilityDepartments();

        // for each facility department (a single facility/department combination), add it to a map of the facilities
        // as keys, each paired with a set of departments at the facility
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

        // convert to the list of public models, each containing the facility and the corresponding list of departments
        return GetFacilitiesAndDepartmentsResult.builder()
                .withFacilitiesAndDepartments(new ModelConverter().toListFacilityDepartments(facilitiesAndDepartments))
                .build();
    }
}
