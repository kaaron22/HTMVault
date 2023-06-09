package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.exceptions.WorkOrderNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.test.helper.WorkOrderTestHelper;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WorkOrderDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private PaginatedQueryList<WorkOrder> workOrders;

    @InjectMocks
    private WorkOrderDao workOrderDao;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void saveWorkOrder_withWorkOrder_callsMapperWithWorkOrder() {
        // GIVEN
        WorkOrder workOrder = new WorkOrder();

        // WHEN
        WorkOrder result = workOrderDao.saveWorkOrder(workOrder);

        // THEN
        verify(dynamoDBMapper).save(workOrder);
        assertEquals(workOrder, result);
    }

    @Test
    public void getWorkOrder_noWorkOrderFoundForWorkOrderId_throwsWorkOrderNotFoundException() {
        // GIVEN
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(WorkOrderNotFoundException.class, () ->
                workOrderDao.getWorkOrder("123"),
                "Expected a request to get a work order for a work order id not found to result in a " +
                        "WorkOrderNotFoundException");
        verify(metricsPublisher).addCount(MetricsConstants.GETWORKORDER_WORKORDERNOTFOUND_COUNT, 1);
    }

    @Test
    public void getWorkOrder_workOrderForWorkOrderIdExists_callsMapperWithPartitionKey() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);
        WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1, "123",
                "G321", manufacturerModel, "TestFacility", "TestDepartment");
        when(dynamoDBMapper.load(eq(WorkOrder.class), anyString())).thenReturn(workOrder);

        // WHEN
        workOrderDao.getWorkOrder(workOrder.getWorkOrderId());


        // THEN
        verify(dynamoDBMapper).load(WorkOrder.class, workOrder.getWorkOrderId());
        verify(metricsPublisher).addCount(MetricsConstants.GETWORKORDER_WORKORDERNOTFOUND_COUNT, 0);
    }

    @Test
    public void getWorkOrders_workOrdersExistForControlNumber_returnsListWorkOrders() {
        // GIVEN
        String controlNumber = "123";

        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(6);

        int numWorkOrdersToGenerate = 4;

        // an array of our generated work orders to return when our mocked paginated query list of work orders
        // is being "converted" to an arraylist
        WorkOrder[] workOrdersArray = new WorkOrder[numWorkOrdersToGenerate];

        // our expected arraylist of work orders
        List<WorkOrder> expected = new ArrayList<>();

        // generate work orders and add to mocked array & expected arraylist
        for (int i = 0; i < numWorkOrdersToGenerate; i++) {
            WorkOrder workOrder = WorkOrderTestHelper.generateWorkOrder(1,
                    HTMVaultServiceUtils.generateId("", 6), "a serial number",
                    manufacturerModel, "TestFacility", "TestDepartment");
            workOrdersArray[i] = workOrder;
            expected.add(workOrder);
        }

        // mocked paginated query list to return
        when(dynamoDBMapper.query(Mockito.eq(WorkOrder.class),
                any(DynamoDBQueryExpression.class))).thenReturn(workOrders);

        // mocked work order array to return when the arraylist constructor attempts to convert the mocked
        // paginated query list
        when(workOrders.toArray()).thenReturn(workOrdersArray);

        // captor for the query expression invoked when we call the method under test
        ArgumentCaptor<DynamoDBQueryExpression<WorkOrder>> captor =
                ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        // WHEN
        List<WorkOrder> result = workOrderDao.getWorkOrders(controlNumber);

        // THEN
        assertEquals(expected, result, "Expected query list of work orders to be what was returned " +
                "from DynamoDB");

        // capture the query expression used in the mapper.query argument
        verify(dynamoDBMapper).query(Mockito.eq(WorkOrder.class), captor.capture());

        // obtain the queryExpression (value) contained in our captured argument
        DynamoDBQueryExpression<WorkOrder> queryExpression = captor.getValue();

        // obtain each specific value the query expression was built with
        String queriedIndexName = queryExpression.getIndexName();

        Map<String, AttributeValue> queriedExpressionAttributes = queryExpression.getExpressionAttributeValues();
        Collection<AttributeValue> expressionAttributeValues = queriedExpressionAttributes.values();
        Set<String> expressionAttributeKeys = queriedExpressionAttributes.keySet();

        String queriedKeyConditionExpression = queryExpression.getKeyConditionExpression();

        boolean queriedConsistentRead = queryExpression.isConsistentRead();

        // verify the expected query expression values
        assertEquals(WorkOrder.CONTROL_NUMBER_WORK_ORDERS_INDEX, queriedIndexName, "Expected query " +
                "expression to query with global secondary index name: " + WorkOrder.CONTROL_NUMBER_WORK_ORDERS_INDEX +
                ", but was: " + queriedIndexName);

        assertTrue(expressionAttributeValues.contains(new AttributeValue(controlNumber)), "Expected query " +
                "expression to set control number to " + controlNumber + "in expression attribute values");

        for (String key : expressionAttributeKeys) {
            assertTrue(queriedKeyConditionExpression.contains(key), "Expected query expression to " +
                    "reference key set in expression attribute values");
        }

        assertFalse(queriedConsistentRead, "Expected query expression to query with consistent reads set " +
                "false");

    }
}
