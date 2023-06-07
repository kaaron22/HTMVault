package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nashss.se.htmvault.utils.CollectionUtils.copyToList;

@Singleton
public class WorkOrderDao {

    private final DynamoDBMapper dynamoDBMapper;
    private final MetricsPublisher metricsPublisher;

    @Inject
    public WorkOrderDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    public List<WorkOrder> getWorkOrders(String controlNumber) {
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":controlNumber", new AttributeValue().withS(controlNumber));
        DynamoDBQueryExpression<WorkOrder> queryExpression = new DynamoDBQueryExpression<WorkOrder>()
                .withIndexName(WorkOrder.CONTROL_NUMBER_WORK_ORDERS_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("controlNumber = :controlNumber")
                .withExpressionAttributeValues(valueMap);

        return copyToList(dynamoDBMapper.query(WorkOrder.class, queryExpression));
    }
}
