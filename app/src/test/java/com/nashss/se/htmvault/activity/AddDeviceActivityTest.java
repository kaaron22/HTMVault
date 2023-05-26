package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.FacilityDepartmentDao;
import com.nashss.se.htmvault.dynamodb.ManufacturerModelDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AddDeviceActivityTest {

    @Mock
    private DeviceDao deviceDao;
    @Mock
    private ManufacturerModelDao manufacturerModelDao;
    @Mock
    private FacilityDepartmentDao facilityDepartmentDao;
    @Mock
    private MetricsPublisher metricsPublisher;

    private AddDeviceActivity addDeviceActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        addDeviceActivity = new AddDeviceActivity(deviceDao, manufacturerModelDao, facilityDepartmentDao,
                metricsPublisher);
    }


}