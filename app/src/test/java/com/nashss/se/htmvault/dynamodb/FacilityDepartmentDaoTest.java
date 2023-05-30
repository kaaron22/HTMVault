package com.nashss.se.htmvault.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class FacilityDepartmentDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;

    @InjectMocks
    private FacilityDepartmentDao facilityDepartmentDao;

    @BeforeEach
    public void setup() {
        initMocks(this);
    }
    @Test
    void getFacilityDepartment_withFacilityAndDepartment_callsMapperWithCompositeKey() {
        // GIVEN
        String facilityName = "TestFacility";
        String assignedDepartment = "TestDepartment";
        FacilityDepartment facilityDepartment = new FacilityDepartment();
        facilityDepartment.setFacilityName(facilityName);
        facilityDepartment.setAssignedDepartment(assignedDepartment);
        when(dynamoDBMapper.load(eq(FacilityDepartment.class), anyString(), anyString()))
                .thenReturn(facilityDepartment);
        //doNothing().when(metricsPublisher).addCount(anyString(), anyInt());

        // WHEN
        FacilityDepartment result = facilityDepartmentDao.getFacilityDepartment(facilityName,
                assignedDepartment);

        // THEN
        verify(dynamoDBMapper).load(FacilityDepartmentDao.class, facilityName, facilityDepartment);
        assertNotNull(result);
    }
}