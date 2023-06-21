package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.utils.CollectionUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FacilityDepartmentDao {

    private final DynamoDBMapper dynamoDBMapper;

    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Facility department dao.
     *
     * @param dynamoDBMapper   the dynamo db mapper
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public FacilityDepartmentDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Gets the facility department specified with the facility/department.
     *
     * @param facility   the facility (hash key)
     * @param department the department (range key)
     * @return the facility department
     */
    public FacilityDepartment getFacilityDepartment(String facility, String department) {
        FacilityDepartment facilityDepartment = dynamoDBMapper.load(FacilityDepartment.class, facility, department);

        if (null == facilityDepartment) {
            metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 1);
            log.info("Could not find a valid facility department in the database for this combination of " +
                    "facility ({}) and department ({}).", facility, department);
            throw new FacilityDepartmentNotFoundException("Could not find a valid facility department for this " +
                    "combination of facility (" + facility + ") and department (" + department + ").");
        }

        metricsPublisher.addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT, 0);
        return facilityDepartment;
    }

    /**
     * Scans for all facility departments, from which the user can select when adding or updating a device. Throws
     * an exception if there was a problem obtaining at least an empty list
     *
     * @return the list facility departments
     */
    public List<FacilityDepartment> getFacilityDepartments() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<FacilityDepartment> facilityDepartments = dynamoDBMapper.scan(FacilityDepartment.class, scanExpression);

        if (null == facilityDepartments) {
            log.info("The list returned when scanning for a list of all facility/departments was null (should be an " +
                    "empty list if none exist in the database).");
            throw new FacilityDepartmentNotFoundException("There was a problem obtaining facility departments.");
        }

        return CollectionUtils.copyToList(facilityDepartments);
    }
}
