package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void getFacilityDepartment_withFacilityAndDepartment_callsMapperWithCompositeKey() {
        // GIVEN
        String facilityName = "TestFacility";
        String assignedDepartment = "TestDepartment";
        FacilityDepartment facilityDepartment = new FacilityDepartment();
        facilityDepartment.setFacilityName(facilityName);
        facilityDepartment.setAssignedDepartment(assignedDepartment);
        when(dynamoDBMapper.load(eq(FacilityDepartment.class), anyString(), anyString()))
                .thenReturn(facilityDepartment);

        // WHEN
        FacilityDepartment result = facilityDepartmentDao.getFacilityDepartment(facilityName,
                assignedDepartment);

        // THEN
        verify(dynamoDBMapper).load(FacilityDepartment.class, facilityName, assignedDepartment);
        verify(metricsPublisher).addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT,
                0);
        assertEquals(facilityDepartment, result);
    }

    @Test
    public void getFacilityDepartment_withInvalidFacilityDepartment_throwsFacilityDepartmentNotFoundException() {
        // GIVEN
        when(dynamoDBMapper.load(eq(FacilityDepartment.class), anyString(), anyString()))
                .thenReturn(null);

        // WHEN & THEN
        assertThrows(FacilityDepartmentNotFoundException.class, () ->
                facilityDepartmentDao.getFacilityDepartment("invalid facility", "invalid department"),
                "Expected mapper load call with facility and department combination not found to result in " +
                        "FacilityDepartmentNotFoundException");
        verify(metricsPublisher).addCount(MetricsConstants.GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT,
                1);
    }
}
