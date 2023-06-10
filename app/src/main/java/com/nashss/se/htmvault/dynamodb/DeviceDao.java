package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
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
import java.util.List;
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
            metricsPublisher.addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 1);
            throw new DeviceNotFoundException("Could not find device with control number " + controlNumber);
        }
        metricsPublisher.addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 0);
        return device;
    }

    public List<Device> searchDevices(String[] criteria) {
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

        if (criteria.length > 0) {
            Map<String, AttributeValue> valueMap = new HashMap<>();
            String valueMapNamePrefix = ":c";

            StringBuilder controlNumberFilterExpression = new StringBuilder();
            StringBuilder serialNumberFilterExpression = new StringBuilder();
            StringBuilder manufacturerModelFilterExpression = new StringBuilder();
            StringBuilder serviceStatusExpression = new StringBuilder();
            StringBuilder facilityNameExpression = new StringBuilder();
            StringBuilder assignedDepartmentExpression = new StringBuilder();
            StringBuilder complianceThroughDateExpression = new StringBuilder();
            StringBuilder nextPmDueDateExpression = new StringBuilder();

            for (int i = 0; i < criteria.length; i++) {
                valueMap.put(valueMapNamePrefix + i,
                        new AttributeValue().withS(criteria[i]));
                controlNumberFilterExpression.append(
                        filterExpressionPart("controlNumber", valueMapNamePrefix, i));
                serialNumberFilterExpression.append(
                        filterExpressionPart("serialNumber", valueMapNamePrefix, i));
                manufacturerModelFilterExpression.append(
                        filterExpressionPart("manufacturerModel", valueMapNamePrefix, i));
                serviceStatusExpression.append(
                        filterExpressionPart("serviceStatus", valueMapNamePrefix, i));
                facilityNameExpression.append(
                        filterExpressionPart("facilityName", valueMapNamePrefix, i));
                assignedDepartmentExpression.append(
                        filterExpressionPart("assignedDepartment", valueMapNamePrefix, i));
                complianceThroughDateExpression.append(
                        filterExpressionPart("complianceThroughDate", valueMapNamePrefix, i));
                nextPmDueDateExpression.append(
                        filterExpressionPart("nextPmDueDate", valueMapNamePrefix, i));
            }

            dynamoDBScanExpression.setExpressionAttributeValues(valueMap);
            dynamoDBScanExpression.setFilterExpression(
                    "(" + controlNumberFilterExpression + ") or (" + serialNumberFilterExpression +
                            ") or (" + manufacturerModelFilterExpression + ") or (" + serviceStatusExpression +
                            ") or (" + facilityNameExpression + ") or (" + assignedDepartmentExpression +
                            ") or (" + complianceThroughDateExpression + ") or (" + nextPmDueDateExpression + ")");
        }

        return this.dynamoDBMapper.scan(Device.class, dynamoDBScanExpression);
    }

    private StringBuilder filterExpressionPart(String target, String valueMapNamePrefix, int position) {
        String possiblyAnd = position == 0 ? "" : "and ";
        return new StringBuilder()
                .append(possiblyAnd)
                .append("contains(")
                .append(target)
                .append(", ")
                .append(valueMapNamePrefix).append(position)
                .append(") ");
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
