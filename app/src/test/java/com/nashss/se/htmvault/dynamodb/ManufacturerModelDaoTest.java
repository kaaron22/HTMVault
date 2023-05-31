package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.exceptions.ManufacturerModelNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class ManufacturerModelDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;

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
}
