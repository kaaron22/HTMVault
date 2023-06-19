package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
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
public class ManufacturerModelDao {

    private final DynamoDBMapper dynamoDBMapper;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Manufacturer model dao.
     *
     * @param dynamoDBMapper   the dynamo db mapper
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public ManufacturerModelDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Gets the manufacturer model specified with the manufacturer/model.
     *
     * @param manufacturer the manufacturer (hash key)
     * @param model        the model (range key)
     * @return the manufacturer model
     */
    public ManufacturerModel getManufacturerModel(String manufacturer, String model) {
        ManufacturerModel manufacturerModel = dynamoDBMapper.load(ManufacturerModel.class, manufacturer, model);

        if (null == manufacturerModel) {
            metricsPublisher.addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT, 1);
            log.info("Could not find a valid manufacturer model in the database for this combination of " +
                    "manufacturer ({}) and model ({})", manufacturer, model);
            throw new ManufacturerModelNotFoundException("Could not find a valid manufacturer model for this " +
                    "combination of manufacturer (" + manufacturer + ") and model (" + model + ")");
        }

        metricsPublisher.addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT, 0);
        return manufacturerModel;
    }

    /**
     * Scans for all manufacturer models, from which the user can select when adding or updating a device. Throws
     * an exception if there was a problem obtaining at least an empty list
     *
     * @return the list of manufacturer models
     */
    public List<ManufacturerModel> getManufacturerModels() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<ManufacturerModel> manufacturerModels = dynamoDBMapper.scan(ManufacturerModel.class, scanExpression);

        if (null == manufacturerModels) {
            log.info("The list returned when scanning for a list of all manufacturer/models was null (should be an " +
                    "empty list if none exist in the database");
            throw new ManufacturerModelNotFoundException("There was a problem obtaining manufacturer models");
        }

        return CollectionUtils.copyToList(manufacturerModels);
    }
}
