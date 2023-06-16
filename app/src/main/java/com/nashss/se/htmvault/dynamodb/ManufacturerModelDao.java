package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.utils.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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

    public List<ManufacturerModel> getManufacturerModels() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        List<ManufacturerModel> manufacturerModels = dynamoDBMapper.scan(ManufacturerModel.class, scanExpression);

        if (null == manufacturerModels) {
            throw new ManufacturerModelNotFoundException("There was a problem obtaining manufacturer models");
        }

        return CollectionUtils.copyToList(manufacturerModels);
    }
}
