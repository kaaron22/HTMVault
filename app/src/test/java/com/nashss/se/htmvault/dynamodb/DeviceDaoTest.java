package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class DeviceDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;

    @InjectMocks
    private DeviceDao deviceDao;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }

    @Test
    public void saveDevice_withDevice_callsMapperWithDevice() {
        // GIVEN
        Device device = new Device();

        // WHEN
        Device result = deviceDao.saveDevice(device);

        // THEN
        verify(dynamoDBMapper).save(device);
        assertEquals(device, result);
    }

    @Test
    public void getDevice_deviceWithControlNumberExists_callsMapperWithPartitionKey() {
        // GIVEN
        String controlNumber = "123";
        when(dynamoDBMapper.load(Mockito.eq(Device.class), eq(controlNumber))).thenReturn(new Device());

        // WHEN
        Device device = deviceDao.getDevice(controlNumber);

        // THEN
        assertNotNull(device);
        verify(dynamoDBMapper).load(Device.class, controlNumber);
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 0);
    }

    @Test
    public void getDevice_deviceNotFound_throwsDeviceNotFoundException() {
        // GIVEN
        String controlNumber = "123";
        when(dynamoDBMapper.load(eq(Device.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertThrows(DeviceNotFoundException.class, () ->
                deviceDao.getDevice(controlNumber),
                "Expected device with control number not found in database to result in a " +
                        "DeviceNotFoundException thrown");
        verify(dynamoDBMapper).load(Device.class, controlNumber);
        verify(metricsPublisher).addCount(MetricsConstants.GETDEVICE_DEVICENOTFOUND_COUNT, 1);
    }
}
