package com.nashss.se.htmvault.dynamodb.models;

import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderSummaryComparatorTest {
    private WorkOrderSummary workOrderSummary1;
    private WorkOrderSummary workOrderSummary2;
    private WorkOrderSummary workOrderSummary3;
    private List<WorkOrderSummary> workOrders;

    @BeforeEach
    void setUp() {
        // an initial completed acceptance work order when the device first arrived and was placed into service
        workOrderSummary1 = new WorkOrderSummary();
        workOrderSummary1.setWorkOrderId("1");
        workOrderSummary1.setWorkOrderType(WorkOrderType.ACCEPTANCE_TESTING);
        workOrderSummary1.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary1.setDateTimeCreated(LocalDateTime.parse("2021-12-03T10:15:30"));
        workOrderSummary1.setCompletionDateTime(LocalDateTime.parse("2021-12-03T11:15:30"));

        // a completed annual preventative maintenance work order
        workOrderSummary2 = new WorkOrderSummary();
        workOrderSummary2.setWorkOrderId("2");
        workOrderSummary2.setWorkOrderType(WorkOrderType.PREVENTATIVE_MAINTENANCE);
        workOrderSummary2.setCompletionStatus(WorkOrderCompletionStatus.CLOSED);
        workOrderSummary2.setDateTimeCreated(LocalDateTime.parse("2022-12-08T10:15:30"));
        workOrderSummary2.setCompletionDateTime(LocalDateTime.parse("2022-12-08T12:15:30"));

        // an open repair work order
        workOrderSummary3 = new WorkOrderSummary();
        workOrderSummary3.setWorkOrderId("7");
        workOrderSummary3.setWorkOrderType(WorkOrderType.REPAIR);
        workOrderSummary3.setCompletionStatus(WorkOrderCompletionStatus.OPEN);
        workOrderSummary3.setDateTimeCreated(LocalDateTime.parse("2023-05-25T13:22:10"));
        workOrderSummary3.setCompletionDateTime(null);

        workOrders =
                new ArrayList<>(Arrays.asList(workOrderSummary1, workOrderSummary3, workOrderSummary2));
    }

    @Test
    public void compare_threeWorkOrdersUnsorted_sortsByWorkOrderIdProperly() {
        // GIVEN
        // setup work orders added to list in the order workOrderSummary1, workOrderSummary3, workOrderSummary2

        // WHEN
        workOrders.sort(new WorkOrderSummaryComparator());

        // THEN
        assertEquals(workOrderSummary1, workOrders.get(0));
        assertEquals(workOrderSummary2, workOrders.get(1));
        assertEquals(workOrderSummary3, workOrders.get(2));
    }

    @Test
    public void reversed_threeWorkOrdersUnsorted_sortsByWorkOrderIdProperly() {
        // GIVEN
        // setup work orders added to list in the order workOrderSummary1, workOrderSummary3, workOrderSummary2

        // WHEN
        workOrders.sort(new WorkOrderSummaryComparator().reversed());

        // THEN
        assertEquals(workOrderSummary3, workOrders.get(0));
        assertEquals(workOrderSummary2, workOrders.get(1));
        assertEquals(workOrderSummary1, workOrders.get(2));
    }
}