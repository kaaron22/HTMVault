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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceDao {

    private final DynamoDBMapper dynamoDBMapper;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Device dao.
     *
     * @param dynamoDBMapper   the dynamo db mapper
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public DeviceDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Saves the device in the database.
     *
     * @param device the device to save
     * @return the device saved
     */
    public Device saveDevice(Device device) {
        dynamoDBMapper.save(device);
        return device;
    }

    /**
     * Gets the device from the database, throwing a DeviceNotFoundException if a device cannot be found
     * for the provided controlNumber.
     *
     * @param controlNumber the control number (hash key for the device)
     * @return the device
     */
    public Device getDevice(String controlNumber) {
        Device device = dynamoDBMapper.load(Device.class, controlNumber);

        if (null == device) {
            metricsPublisher.addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 1);
            log.info("An attempt was made to obtain a device with control number ({}), but could not be " +
                    "found", controlNumber);
            throw new DeviceNotFoundException("Could not find device with control number " + controlNumber);
        }
        metricsPublisher.addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 0);
        return device;
    }

    // from project template, modified for search devices endpoint
    /**
     * Scans the devices database table for a list of devices matching the search criteria.
     *
     * @param criteria the criteria for which to scan the table for matching values
     * @return the list of devices matching the criteria
     */
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

    // from project template, modified for search devices endpoint
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

    /**
     * Checks to see if the device matching the manufacturer, model, and serial number was previously added. If so,
     * throws a DevicePreviouslyAddedException
     *
     * @param manufacturerModel the manufacturer model with which to compare
     * @param serialNumber      the serial number with which to compare
     */
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
            log.info("An attempt was made to add a device with this manufacturer, model, and serial number " +
                    "was previously added: Manufacturer/Model ({}), SerialNumber ({})", manufacturerModel,
                    serialNumber);
            throw new DevicePreviouslyAddedException("A device with this manufacturer, model, and serial number was " +
                    "previously added");
        }
    }
}
