package com.nashss.se.htmvault.converters;

import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderSummaryListConverterTest {

    private final LocalDateTime basis = LocalDateTime.of(LocalDate.of(2023, 5, 30),
            LocalTime.of(15, 16, 30, 579893000));

    private WorkOrderSummary workOrderSummary1;
    private WorkOrderSummary workOrderSummary2;
    private final WorkOrderSummaryListConverter workOrderSummaryListConverter = new WorkOrderSummaryListConverter();

    @BeforeEach
    public void setup() {
        workOrderSummary1 = new WorkOrderSummary();
        workOrderSummary1.setWorkOrderId("1");
        workOrderSummary1.setWorkOrderType(WorkOrderType.ACCEPTANCE_TESTING);
        workOrderSummary1.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary1.setDateTimeCreated(basis.minusDays(3));
        workOrderSummary1.setCompletionDateTime(basis.minusDays(2));

        workOrderSummary2 = new WorkOrderSummary();
        workOrderSummary2.setWorkOrderId("2");
        workOrderSummary2.setWorkOrderType(WorkOrderType.REPAIR);
        workOrderSummary2.setCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrderSummary2.setDateTimeCreated(basis.minusDays(1));
        workOrderSummary2.setCompletionDateTime(null);
    }

    @Test
    public void convert_emptyListWorkOrderSummary_returnsExpectedString() {
        // GIVEN
        List<WorkOrderSummary> workOrderSummaryList = new ArrayList<>();

        // WHEN
        String serializedWorkOrderList = workOrderSummaryListConverter.convert(workOrderSummaryList);

        // THEN
        assertEquals("[]", serializedWorkOrderList);
    }

    @Test
    public void convert_listTwoWorkOrderSummaries_returnsExpectedString() {
        // GIVEN
        List<WorkOrderSummary> workOrderSummaryList =
                new ArrayList<>(Arrays.asList(workOrderSummary1, workOrderSummary2));

        // WHEN
        String serializedWorkOrderSummaries = workOrderSummaryListConverter.convert(workOrderSummaryList);

        // THEN
        assertEquals("[{\"workOrderId\":\"1\",\"workOrderType\":\"ACCEPTANCE_TESTING\"," +
                "\"completionStatus\":\"CLOSED\",\"dateTimeCreated\":{\"date\":{\"year\":2023,\"month\":5,\"day\":27}," +
                "\"time\":{\"hour\":15,\"minute\":16,\"second\":30,\"nano\":579893000}}," +
                "\"completionDateTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":28},\"time\":{\"hour\":15," +
                "\"minute\":16,\"second\":30,\"nano\":579893000}}},{\"workOrderId\":\"2\",\"workOrderType\":\"REPAIR\"," +
                "\"completionStatus\":\"OPEN\",\"dateTimeCreated\":{\"date\":{\"year\":2023,\"month\":5,\"day\":29}," +
                "\"time\":{\"hour\":15,\"minute\":16,\"second\":30,\"nano\":579893000}}}]",
                serializedWorkOrderSummaries);
    }

    @Test
    public void unconvert_jsonWorkOrderSummaries_returnsListWorkOrderSummaries() {
        // GIVEN
        String serializedWorkOrderSummaries = "[{\"workOrderId\":\"1\",\"workOrderType\":\"ACCEPTANCE_TESTING\"," +
                "\"completionStatus\":\"CLOSED\",\"dateTimeCreated\":{\"date\":{\"year\":2023,\"month\":5,\"day\":27}," +
                "\"time\":{\"hour\":15,\"minute\":16,\"second\":30,\"nano\":579893000}}," +
                "\"completionDateTime\":{\"date\":{\"year\":2023,\"month\":5,\"day\":28},\"time\":{\"hour\":15," +
                "\"minute\":16,\"second\":30,\"nano\":579893000}}},{\"workOrderId\":\"2\",\"workOrderType\":\"REPAIR\"," +
                "\"completionStatus\":\"OPEN\",\"dateTimeCreated\":{\"date\":{\"year\":2023,\"month\":5,\"day\":29}," +
                "\"time\":{\"hour\":15,\"minute\":16,\"second\":30,\"nano\":579893000}}}]";

        // WHEN
        List<WorkOrderSummary> workOrderSummaryList =
                workOrderSummaryListConverter.unconvert(serializedWorkOrderSummaries);
        List<WorkOrderSummary> expected = new ArrayList<>(Arrays.asList(workOrderSummary1, workOrderSummary2));

        // THEN
        assertEquals(expected, workOrderSummaryList);
    }
}