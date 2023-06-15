package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetManufacturersAndModelsRequest;
import com.nashss.se.htmvault.activity.results.GetManufacturersAndModelsResult;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.*;

public class GetManufacturersAndModelsActivity {

    private final ManufacturerModelDao manufacturerModelDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public GetManufacturersAndModelsActivity(ManufacturerModelDao manufacturerModelDao,
                                             MetricsPublisher metricsPublisher) {
        this.manufacturerModelDao = manufacturerModelDao;
        this.metricsPublisher = metricsPublisher;
    }

    public GetManufacturersAndModelsResult handleRequest(final GetManufacturersAndModelsRequest request) {
        List<ManufacturerModel> manufacturerModels = manufacturerModelDao.getManufacturerModels();

        Map<String, Set<String>> manufacturersAndModels = new HashMap<>();
        for (ManufacturerModel manufacturerModel : manufacturerModels) {
            if (!manufacturersAndModels.containsKey(manufacturerModel.getManufacturer())) {
                manufacturersAndModels.put(manufacturerModel.getManufacturer(),
                        new HashSet<>(List.of(manufacturerModel.getModel())));
            } else {
                Set<String> models = manufacturersAndModels.get(manufacturerModel.getManufacturer());
                models.add(manufacturerModel.getModel());
                manufacturersAndModels.put(manufacturerModel.getManufacturer(), models);
            }
        }

        return GetManufacturersAndModelsResult.builder()
                .withManufacturersAndModels(manufacturersAndModels)
                .build();
    }
}
