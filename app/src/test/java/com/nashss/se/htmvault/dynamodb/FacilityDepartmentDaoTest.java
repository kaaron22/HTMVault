package com.nashss.se.htmvault.dynamodb;

import com.nashss.se.htmvault.dynamodb.models.FacilityDepartment;
import com.nashss.se.htmvault.exceptions.FacilityDepartmentNotFoundException;
import com.nashss.se.htmvault.metrics.MetricsConstants;
import com.nashss.se.htmvault.metrics.MetricsPublisher;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class FacilityDepartmentDaoTest {

    @Mock
    private DynamoDBMapper dynamoDBMapper;
    @Mock
    private MetricsPublisher metricsPublisher;
    @Mock
    private PaginatedScanList<FacilityDepartment> facilityDepartments;

    @InjectMocks
    private FacilityDepartmentDao facilityDepartmentDao;

    @BeforeEach
    public void setup() {
        openMocks(this);
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

    @Test
    public void getFacilityDepartments_facilityDepartmentsExist_returnsList() {
        // GIVEN
        // the facility department objects that we are obtaining by DDB Scan
        FacilityDepartment facilityDepartment1 = new FacilityDepartment();
        facilityDepartment1.setFacilityName("Test Hospital");
        facilityDepartment1.setAssignedDepartment("ICU");

        FacilityDepartment facilityDepartment2 = new FacilityDepartment();
        facilityDepartment1.setFacilityName("Test Hospital");
        facilityDepartment1.setAssignedDepartment("ER");

        FacilityDepartment facilityDepartment3 = new FacilityDepartment();
        facilityDepartment1.setFacilityName("Test Clinic");
        facilityDepartment1.setAssignedDepartment("Convenient Care");

        FacilityDepartment facilityDepartment4 = new FacilityDepartment();
        facilityDepartment1.setFacilityName("Test Surgical Center");
        facilityDepartment1.setAssignedDepartment("Sterile Processing");

        // an array of our FacilityDepartment objects to return when our mocked paginated scan list of
        // FacilityDepartments is being "converted" to an arraylist
        FacilityDepartment[] facilityDepartmentsArray = new FacilityDepartment[4];
        facilityDepartmentsArray[0] = facilityDepartment1;
        facilityDepartmentsArray[1] = facilityDepartment2;
        facilityDepartmentsArray[2] = facilityDepartment3;
        facilityDepartmentsArray[3] = facilityDepartment4;

        // our expected arraylist of FacilityDepartments
        List<FacilityDepartment> expected = new ArrayList<>(Arrays.asList(facilityDepartment1, facilityDepartment2,
                facilityDepartment3, facilityDepartment4));

        // mocked paginated scan list to return
        when(dynamoDBMapper.scan(Mockito.eq(FacilityDepartment.class),
                any(DynamoDBScanExpression.class))).thenReturn(facilityDepartments);

        // mocked facility department array to return when the arraylist constructor attempts to convert the mocked
        // paginated scan list
        when(facilityDepartments.toArray()).thenReturn(facilityDepartmentsArray);

        // WHEN
        List<FacilityDepartment> result = facilityDepartmentDao.getFacilityDepartments();

        // THEN
        assertEquals(expected, result, "Expected scan list of facility departments to be what was returned " +
                "from DynamoDB");
    }
}
