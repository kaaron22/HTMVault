package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.nashss.se.htmvault.utils.CollectionUtils.copyToList;

@Singleton
public class WorkOrderDao {

    private final DynamoDBMapper dynamoDBMapper;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Work order dao.
     *
     * @param dynamoDBMapper   the dynamo db mapper
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public WorkOrderDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Saves the work order in the database.
     *
     * @param workOrder the work order to save
     * @return the work order saved
     */
    public WorkOrder saveWorkOrder(WorkOrder workOrder) {
        dynamoDBMapper.save(workOrder);
        return workOrder;
    }

    /**
     * Gets the work order from the database, throwing a WorkOrderNotFoundException if a work order cannot be found
     * for the provided work order id.
     *
     * @param workOrderId the work order id
     * @return the work order
     */
    public WorkOrder getWorkOrder(String workOrderId) {
        WorkOrder workOrder = dynamoDBMapper.load(WorkOrder.class, workOrderId);

        if (null == workOrder) {
            metricsPublisher.addCount(MetricsConstants.GETWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
            log.info("An attempt was made to obtain a work order with work order id {}, but could not be " +
                    "found", workOrderId);
            throw new WorkOrderNotFoundException("Could not find work order for work order id provided: " +
                    workOrderId);
        }

        metricsPublisher.addCount(MetricsConstants.GETWORKORDER_WORKORDERNOTFOUND_COUNT, 0);
        return workOrder;
    }

    /**
     * Gets the work orders for a given device, matching by the device id.
     *
     * @param controlNumber the device's control number (device id)
     * @return the list of existing work orders for this device
     */
    public List<WorkOrder> getWorkOrders(String controlNumber) {
        // the map of key attributes paired with their actual values (in this case, a GSI with partition key of device
        // id only)
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":controlNumber", new AttributeValue().withS(controlNumber));

        DynamoDBQueryExpression<WorkOrder> queryExpression = new DynamoDBQueryExpression<WorkOrder>()
                // the GSI we're using
                .withIndexName(WorkOrder.CONTROL_NUMBER_WORK_ORDERS_INDEX)
                // strongly consistent reads (to ensure an item is up-to-date) are not supported when querying using
                // GSIs
                .withConsistentRead(false)
                // the condition (searching for work orders that match the hash key value in our map)
                .withKeyConditionExpression("controlNumber = :controlNumber")
                // the map to find the value for the key
                .withExpressionAttributeValues(valueMap);

        return copyToList(dynamoDBMapper.query(WorkOrder.class, queryExpression));
    }
}
