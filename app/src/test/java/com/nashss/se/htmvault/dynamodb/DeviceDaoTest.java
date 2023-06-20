package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.converters.ManufacturerModelConverter;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.DevicePreviouslyAddedException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class DeviceDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private PaginatedQueryList<Device> queryList;

    @InjectMocks
    private DeviceDao deviceDao;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void saveDevice_withDevice_callsMapperWithDevice() {
        // GIVEN
        Device device = new Device();

        // WHEN
        Device result = deviceDao.saveDevice(device);

        // THEN
        verify(dynamoDBMapper).save(device);
        assertEquals(device, result);
    }

    @Test
    public void getDevice_deviceWithControlNumberExists_callsMapperWithPartitionKey() {
        // GIVEN
        String controlNumber = "123";
        when(dynamoDBMapper.load(Mockito.eq(Device.class), eq(controlNumber))).thenReturn(new Device());

        // WHEN
        Device device = deviceDao.getDevice(controlNumber);

        // THEN
        assertNotNull(device);
        verify(dynamoDBMapper).load(Device.class, controlNumber);
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 0);
    }

    @Test
    public void getDevice_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        String controlNumber = "123";
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                deviceDao.getDevice(controlNumber),
                "Expected device with control number not found in database to result in a " +
                        "DeviceNotFoundException thrown");
        verify(dynamoDBMapper).load(Device.class, controlNumber);
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 1);
    }

    @Test
    public void checkDevicePreviouslyAdded_deviceFound_throwsDevicePreviouslyAddedException() {
        // GIVEN
        String serialNumber = "G321";

        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("Monitor Co.");
        manufacturerModel.setModel("Their First Monitor Model");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);

        // mocked paginated query list to return
        when(dynamoDBMapper.query(eq(Device.class), any(DynamoDBQueryExpression.class))).thenReturn(queryList);

        // captor for the query expression invoked when we call the method under test
        ArgumentCaptor<DynamoDBQueryExpression<Device>> captor = ArgumentCaptor.forClass(DynamoDBQueryExpression.class);

        // WHEN & THEN
        assertThrows(DevicePreviouslyAddedException.class, () ->
                deviceDao.checkDevicePreviouslyAdded(manufacturerModel, serialNumber),
                "Expected attempting to create/add a new device that matches one already" +
                        "in the database by serial number and manufacturer/model to result in a" +
                        "DevicePreviouslyAddedException thrown");

        // capture the query expression used in the mapper.query argument
        verify(dynamoDBMapper).query(eq(Device.class), captor.capture());

        // obtain the queryExpression (value) contained in our captured argument
        DynamoDBQueryExpression<Device> queryExpression = captor.getValue();

        // obtain each specific value the query expression was built with
        String queriedIndexName = queryExpression.getIndexName();

        Map<String, AttributeValue> queriedExpressionAttributes = queryExpression.getExpressionAttributeValues();
        Collection<AttributeValue> expressionAttributeValues = queriedExpressionAttributes.values();
        Set<String> expressionAttributeKeys = queriedExpressionAttributes.keySet();

        String queriedKeyConditionExpression = queryExpression.getKeyConditionExpression();

        boolean queriedConsistentRead = queryExpression.isConsistentRead();

        // verify the expected query expression values
        assertEquals(Device.MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX, queriedIndexName, "Expected query " +
                "expression to query with global secondary index name: " +
                Device.MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX + ", but was: " + queriedIndexName);

        assertTrue(expressionAttributeValues.contains(new AttributeValue(serialNumber)), "Expected query " +
                "expression to set serial number to " + serialNumber + "in expression attribute values");
        assertTrue(expressionAttributeValues.contains(new AttributeValue().withS(new ManufacturerModelConverter()
                        .convert(manufacturerModel))),
                "Expected query expression to set manufacturer model to " + manufacturerModel + "in " +
                        "expression attribute values");

        for (String key : expressionAttributeKeys) {
            assertTrue(queriedKeyConditionExpression.contains(key), "Expected query expression to " +
                    "reference key set in expression attribute values");
        }

        assertFalse(queriedConsistentRead, "Expected query expression to query with consistent reads set " +
                "false");
    }
}
