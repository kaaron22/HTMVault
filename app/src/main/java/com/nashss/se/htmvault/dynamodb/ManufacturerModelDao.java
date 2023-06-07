package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ManufacturerModelDao {

    private final DynamoDBMapper dynamoDBMapper;

    private final MetricsPublisher metricsPublisher;

    @Inject
    public ManufacturerModelDao(DynamoDBMapper dynamoDBMapper, MetricsPublisher metricsPublisher) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.metricsPublisher = metricsPublisher;
    }

    public ManufacturerModel getManufacturerModel(String manufacturer, String model) {
        ManufacturerModel manufacturerModel = dynamoDBMapper.load(ManufacturerModel.class, manufacturer, model);

        if (null == manufacturerModel) {
            metricsPublisher.addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT, 1);
            throw new ManufacturerModelNotFoundException("Could not find a valid manufacturer model for this " +
                    "combination of manufacturer (" + manufacturer + ") and model (" + model + ")");
        }

        metricsPublisher.addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT, 0);
        return manufacturerModel;
    }
}
