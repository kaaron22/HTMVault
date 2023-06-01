package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceWithControlNumberAlreadyExistsException;
import com.nashss.se.htmvault.exceptions.InvalidAttributeValueException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public void checkDeviceWithControlNumberAlreadyExists(String controlNumber) {
        Device device = dynamoDBMapper.load(Device.class, controlNumber);

        if (null != device) {
            throw new DeviceWithControlNumberAlreadyExistsException(String.format("The %s provided (%s) already exists",
                    "Control Number", controlNumber));
        }
    }
}
