package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetManufacturersAndModelsRequest;
import com.nashss.se.htmvault.activity.results.GetManufacturersAndModelsResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class GetManufacturersAndModelsActivity {

    private final ManufacturerModelDao manufacturerModelDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Get manufacturers and models activity.
     *
     * @param manufacturerModelDao the manufacturer model dao
     * @param metricsPublisher     the metrics publisher
     */
    @Inject
    public GetManufacturersAndModelsActivity(ManufacturerModelDao manufacturerModelDao,
                                             MetricsPublisher metricsPublisher) {
        this.manufacturerModelDao = manufacturerModelDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to get a full list of individual manufacturer/model objects from the database table,
     * converting the list to a list of objects that each contain a manufacturer name and a list of the models
     * associated with the facility.
     * Propagates a ManufacturerModelNotFoundException.
     *
     * @param request the request
     * @return the get manufacturers and models result
     */
    public GetManufacturersAndModelsResult handleRequest(final GetManufacturersAndModelsRequest request) {
        log.info("Received GetManufacturersAndModelsRequest {}", request);

        List<ManufacturerModel> manufacturerModels = manufacturerModelDao.getManufacturerModels();

        // for each manufacturer model (a single manufacturer/model combination), add it to a map of the manufacturers
        // as keys, each paired with a set of corresponding models
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

        // convert to the list of public models, each containing the manufacturer and manufacturer's list of models
        return GetManufacturersAndModelsResult.builder()
                .withManufacturersAndModels(new ModelConverter().toListManufacturerModels(manufacturersAndModels))
                .build();
    }
}
