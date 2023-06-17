package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.utils.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class FacilityDepartmentDao {

    private final DynamoDBMapper dynamoDBMapper;

    private final MetricsPublisher metricsPublisher;

    @Inject
    public FacilityDepartmentDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    public FacilityDepartment getFacilityDepartment(String facility, String department) {
        FacilityDepartment facilityDepartment = dynamoDBMapper.load(FacilityDepartment.class, facility, department);

        if (null == facilityDepartment) {
            metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 1);
            throw new FacilityDepartmentNotFoundException("Could not find a valid facility department for this " +
                    "combination of facility (" + facility + ") and department (" + department + ")");
        }

        metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 0);
        return facilityDepartment;
    }

    public List<FacilityDepartment> getFacilityDepartments() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<FacilityDepartment> facilityDepartments = dynamoDBMapper.scan(FacilityDepartment.class, scanExpression);

        if (null == facilityDepartments) {
            throw new FacilityDepartmentNotFoundException("There was a problem obtaining facility departments");
        }

        return CollectionUtils.copyToList(facilityDepartments);
    }
}
