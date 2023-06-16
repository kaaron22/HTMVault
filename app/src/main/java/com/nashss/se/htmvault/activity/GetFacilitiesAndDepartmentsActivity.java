package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

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
}
