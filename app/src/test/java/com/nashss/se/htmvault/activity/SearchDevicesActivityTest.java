package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.test.helper.DeviceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.MockitoAnnotations.openMocks;

class SearchDevicesActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock

    private SearchDevicesActivity searchDevicesActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        searchDevicesActivity = new SearchDevicesActivity(deviceDao, metricsPublisher);
    }

    @Test
    public void handleRequest_resultsExistForCriteria_returnsListOfDeviceResults() {
        // GIVEN
        ManufacturerModel manufacturerModel = new ManufacturerModel();
        manufacturerModel.setManufacturer("TestManufacturer");
        manufacturerModel.setModel("TestModel");
        manufacturerModel.setRequiredMaintenanceFrequencyInMonths(12);
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            devices.add(DeviceTestHelper.generateActiveDevice(i, manufacturerModel, "TestFacility",
                    "TestDepartment"));
        }

        String[] criteria = new String[1];
        criteria[0] = "TestManufacturer";
        SearchDevicesRequest searchDevicesRequest = SearchDevicesRequest.builder()
                .withCriteria("TestManufacturer")
                .build();
        when(deviceDao.searchDevices(criteria)).thenReturn(devices);

        // WHEN
        SearchDevicesResult searchDevicesResult = searchDevicesActivity.handleRequest(searchDevicesRequest);
        List<DeviceModel> deviceModelList = searchDevicesResult.getDevices();

        // THEN
        DeviceTestHelper.assertDevicesEqualDeviceModels(devices, deviceModelList);
    }

    @Test
    public void handleRequest_withNullCriteria_isIdenticalToEmptyCriteria() {
        // GIVEN
        String criteria = null;
        ArgumentCaptor<String[]> criteriaArray = ArgumentCaptor.forClass(String[].class);

        when(deviceDao.searchDevices(criteriaArray.capture())).thenReturn(List.of());

        SearchDevicesRequest request = SearchDevicesRequest.builder()
                .withCriteria(criteria)
                .build();

        // WHEN
        SearchDevicesResult result = searchDevicesActivity.handleRequest(request);

        // THEN
        assertEquals(0, criteriaArray.getValue().length, "Criteria Array should be empty");
    }
}