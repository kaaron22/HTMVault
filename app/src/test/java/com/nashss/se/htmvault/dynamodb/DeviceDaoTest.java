package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.exceptions.DeviceWithControlNumberAlreadyExistsException;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void checkDeviceWithControlNumberAlreadyExists_doesNotYetExist_doesNotThrowException() {
        // GIVEN
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(null);

        // WHEN & THEN
        assertDoesNotThrow(() -> deviceDao.checkDeviceWithControlNumberAlreadyExists("123"),
                "Expected a device with a control number does not exist in the database to not result in an " +
                        "exception thrown");
    }

    @Test
    public void checkDeviceWithControlNumberAlreadyExists_exists_throwsDeviceWithControlNumberAlreadyExistsException() {
        // GIVEN
        when(dynamoDBMapper.load(Mockito.eq(Device.class), anyString())).thenReturn(new Device());

        // WHEN & THEN
        assertThrows(DeviceWithControlNumberAlreadyExistsException.class, () ->
                deviceDao.checkDeviceWithControlNumberAlreadyExists("123"),
                "Expected a device with this control number exists in the database to not result in a" +
                        "DeviceWithControlNumberAlreadyExistsException thrown");
    }
}
