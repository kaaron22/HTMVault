package com.nashss.se.htmvault.activity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ReactivateDeviceActivityTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private DeviceDao deviceDao;
    @Mock
    private MetricsPublisher metricsPublisher;
    private ReactivateDeviceActivity reactivateDeviceActivity;

    @BeforeEach
    void setUp() {
        openMocks(this);
        reactivateDeviceActivity = new ReactivateDeviceActivity(deviceDao, metricsPublisher);
    }

    @Test

}