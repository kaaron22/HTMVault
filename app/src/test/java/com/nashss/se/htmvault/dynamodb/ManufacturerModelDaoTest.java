package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ManufacturerModelDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private PaginatedScanList<ManufacturerModel> manufacturerModels;

    @InjectMocks
    private ManufacturerModelDao manufacturerModelDao;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }
    @Test
    public void getManufacturerModel_withManufacturerAndModel_callsMapperWithCompositeKey() {
        // GIVEN
        String manufacturer = "TestManufacturer";
        String model = "TestModel";
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer(manufacturer);
        manufacturerModel.setModel(model);
        when(dynamoDBMapper.load(eq(ManufacturerModel.class), anyString(), anyString()))
                .thenReturn(manufacturerModel);

        // WHEN
        ManufacturerModel result = manufacturerModelDao.getManufacturerModel(manufacturer, model);

        // THEN
        verify(dynamoDBMapper).load(ManufacturerModel.class, manufacturer, model);
        verify(metricsPublisher).addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT,
                0);
        assertEquals(manufacturerModel, result);
    }

    @Test
    public void getManufacturerModel_withInvalidManufacturerModel_throwsManufacturerModelNotFoundException() {
        // GIVEN
        when(dynamoDBMapper.load(eq(ManufacturerModel.class), anyString(), anyString()))
                .thenReturn(null);

        // WHEN & THEN
        assertThrows(ManufacturerModelNotFoundException.class, () ->
                        manufacturerModelDao.getManufacturerModel("invalid manufacturer",
                                "invalid model"),
                "Expected mapper load call with manufacturer and model combination not found to result in " +
                        "ManufacturerModelNotFoundException");
        verify(metricsPublisher).addCount(MetricsConstants.GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT,
                1);
    }

    @Test
    public void getManufacturerModels_manufacturerModelsExist_returnsList() {
        // GIVEN
        // the manufacturer model objects that we are obtaining by DDB Scan
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

        // an array of our ManufacturerModel objects to return when our mocked paginated scan list of ManufacturerModels
        // is being "converted" to an arraylist
        ManufacturerModel[] manufacturerModelsArray = new ManufacturerModel[4];
        manufacturerModelsArray[0] = manufacturerModel1;
        manufacturerModelsArray[1] = manufacturerModel2;
        manufacturerModelsArray[2] = manufacturerModel3;
        manufacturerModelsArray[3] = manufacturerModel4;

        // our expected arraylist of ManufacturerModels
        List<ManufacturerModel> expected = new ArrayList<>(Arrays.asList(manufacturerModel1,
                manufacturerModel2, manufacturerModel3, manufacturerModel4));

        // mocked paginated scan list to return
        when(dynamoDBMapper.scan(Mockito.eq(ManufacturerModel.class),
                any(DynamoDBScanExpression.class))).thenReturn(manufacturerModels);

        // mocked manufacturer model array to return when the arraylist constructor attempts to convert the mocked
        // paginated scan list
        when(manufacturerModels.toArray()).thenReturn(manufacturerModelsArray);

        // WHEN
        List<ManufacturerModel> result = manufacturerModelDao.getManufacturerModels();

        // THEN
        assertEquals(expected, result, "Expected scan list of manufacturer models to be what was returned " +
                "from DynamoDB");
    }
}
