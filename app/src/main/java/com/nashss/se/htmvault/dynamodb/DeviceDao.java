package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.nashss.se.htmvault.converters.ManufacturerModelConverter;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.exceptions.DevicePreviouslyAddedException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DeviceDao {

    private final DynamoDBMapper dynamoDBMapper;

    private final MetricsPublisher metricsPublisher;

    @Inject
    public DeviceDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    public Device saveDevice(Device device) {
        dynamoDBMapper.save(device);
        return device;
    }

    public Device getDevice(String controlNumber) {
        Device device = dynamoDBMapper.load(Device.class, controlNumber);

        if (null == device) {
            metricsPublisher.addCount(MetricsConstants.GETDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 1);
            throw new DeviceNotFoundException("Could not find device with control number " + controlNumber);
        }
        metricsPublisher.addCount(MetricsConstants.GETDEVICE_INVALIDATTRIBUTEVALUE_COUNT, 0);
        return device;
    }

    public void checkDevicePreviouslyAdded(ManufacturerModel manufacturerModel, String serialNumber) {
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":manufacturerModel",
                new AttributeValue().withS(new ManufacturerModelConverter().convert(manufacturerModel)));
        valueMap.put(":serialNumber", new AttributeValue().withS(serialNumber));
        DynamoDBQueryExpression<Device> queryExpression = new DynamoDBQueryExpression<Device>()
                .withIndexName(Device.MANUFACTURER_MODEL_SERIAL_NUMBER_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("manufacturerModel = :manufacturerModel and serialNumber = :serialNumber")
                .withExpressionAttributeValues(valueMap);

        PaginatedQueryList<Device> deviceList = dynamoDBMapper.query(Device.class, queryExpression);

        if (!deviceList.isEmpty()) {
            throw new DevicePreviouslyAddedException("A device with this manufacturer, model, and serial number was " +
                    "previously added");
        }
    }
}
