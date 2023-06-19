package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.GetManufacturersAndModelsRequest;
import com.nashss.se.htmvault.activity.results.GetManufacturersAndModelsResult;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.ManufacturerModels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class GetManufacturersAndModelsActivityTest {

    @Mock
    private ManufacturerModelDao manufacturerModelDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private GetManufacturersAndModelsActivity getManufacturersAndModelsActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        getManufacturersAndModelsActivity =
                new GetManufacturersAndModelsActivity(manufacturerModelDao, metricsPublisher);
    }

    @Test
    public void handleRequest_requestForManufacturersAndModels_returnsSortedListOfManufacturersAndModelsInResult() {
        // GIVEN
        GetManufacturersAndModelsRequest request = GetManufacturersAndModelsRequest.builder()
                .build();

        ManufacturerModel manufacturerModel1 = new ManufacturerModel();
        manufacturerModel1.setManufacturer("Monitor Co.");
        manufacturerModel1.setModel("Their First Monitor Model");
        manufacturerModel1.setRequiredMaintenanceFrequencyInMonths(12);

        ManufacturerModel manufacturerModel2 = new ManufacturerModel();
        manufacturerModel2.setManufacturer("Monitor Co.");
        manufacturerModel2.setModel("Their Second Monitor Model");
        manufacturerModel2.setRequiredMaintenanceFrequencyInMonths(12);

        ManufacturerModel manufacturerModel3 = new ManufacturerModel();
        manufacturerModel3.setManufacturer("Defibrillator Co.");
        manufacturerModel3.setModel("Their Only Defibrillator Model");
        manufacturerModel3.setRequiredMaintenanceFrequencyInMonths(6);

        ManufacturerModel manufacturerModel4 = new ManufacturerModel();
        manufacturerModel4.setManufacturer("A Different Monitor Co.");
        manufacturerModel4.setModel("Their First Monitor Model");
        manufacturerModel4.setRequiredMaintenanceFrequencyInMonths(12);

        List<ManufacturerModel> manufacturerModelList = new ArrayList<>(Arrays.asList(manufacturerModel1,
                manufacturerModel2, manufacturerModel3, manufacturerModel4));

        when(manufacturerModelDao.getManufacturerModels()).thenReturn(manufacturerModelList);

        // WHEN
        GetManufacturersAndModelsResult result = getManufacturersAndModelsActivity.handleRequest(request);
        ManufacturerModels manufacturerModels1 = ManufacturerModels.builder()
                .withManufacturer("Monitor Co.")
                .withModels(new ArrayList<>(Arrays.asList("Their First Monitor Model", "Their Second Monitor Model")))
                .build();
        ManufacturerModels manufacturerModels2 = ManufacturerModels.builder()
                .withManufacturer("Defibrillator Co.")
                .withModels(new ArrayList<>(List.of("Their Only Defibrillator Model")))
                .build();
        ManufacturerModels manufacturerModels3 = ManufacturerModels.builder()
                .withManufacturer("A Different Monitor Co.")
                .withModels(new ArrayList<>(List.of("Their First Monitor Model")))
                .build();
        List<ManufacturerModels> expected = new ArrayList<>(Arrays.asList(manufacturerModels3, manufacturerModels2,
                manufacturerModels1));
        List<ManufacturerModels> results = result.getManufacturersAndModels();

        // THEN
        for (int i = 0; i < results.size(); i++) {
            assertTrue(results.contains(expected.get(i)));
        }
    }

    @Test
    public void handleRequest_nullListOfManufacturerModels_throwsManufacturerModelNotFoundException() {
        // GIVEN
        GetManufacturersAndModelsRequest request = GetManufacturersAndModelsRequest.builder()
                        .build();
        when(manufacturerModelDao.getManufacturerModels()).thenThrow(ManufacturerModelNotFoundException.class);

        // WHEN & THEN
        assertThrows(ManufacturerModelNotFoundException.class, () ->
                getManufacturersAndModelsActivity.handleRequest(request),
                "Expected a request to get manufacturers and models that results in a " +
                        "ManufacturerModelNotFoundException to propagate");
    }
}
