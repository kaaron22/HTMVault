package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

public class FacilityDepartmentDao {

    private final DynamoDBMapper dynamoDBMapper;

    private final MetricsPublisher metricsPublisher;

    public FacilityDepartmentDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    public FacilityDepartment getFacilityDepartment(String facility, String department) {
        FacilityDepartment facilityDepartment = dynamoDBMapper.load(FacilityDepartment.class, facility, department);

        if (null == facilityDepartment) {
            metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 1);
            throw new ManufacturerModelNotFoundException("Could not find a valid facility department for this " +
                    "combination of facility (" + facility + ") and department (" + department + ")");
        }

        metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 0);
        return facilityDepartment;
    }
}
