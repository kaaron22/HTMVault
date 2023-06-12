package com.nashss.se.htmvault.dynamodb.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderCompletionDateTimeComparatorTest {

    WorkOrderCompletionDateTimeComparator workOrderCompletionDateTimeComparator =
            new WorkOrderCompletionDateTimeComparator();

    @Test
    void compare_twoWorkOrdersWithNullCompletionDateTimes_returnsCorrectComparisonValue() {
        // GIVEN
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setCreationDateTime(LocalDateTime.of(LocalDate.of(2023, 6, 12),
                LocalTime.of(17, 10, 5)));
        workOrder1.setCompletionDateTime(null);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setCreationDateTime(LocalDateTime.of(LocalDate.of(2023, 6, 12),
                LocalTime.of(17, 10, 6)));
        workOrder2.setCompletionDateTime(null);

        // WHEN
        int result = workOrderCompletionDateTimeComparator.compare(workOrder1, workOrder2);

        // THEN
        assertTrue(result < 0);
    }

    @Test
    void compare_twoWorkOrdersOneWithNullCompletionDateTime_returnsCorrectComparisonValue() {
        // GIVEN
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setWorkOrderId("WR012");
        workOrder1.setCompletionDateTime(null);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setWorkOrderId("WR013");
        workOrder2.setCompletionDateTime(LocalDateTime.now());

        // WHEN
        int result = workOrderCompletionDateTimeComparator.compare(workOrder1, workOrder2);

        // THEN
        assertTrue(result > 0);
    }

    @Test
    void compare_twoWorkOrdersWithUniqueCompletionDateTimes_returnsCorrectComparisonValue() {
        // GIVEN
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setWorkOrderId("WR012");
        workOrder1.setCompletionDateTime(LocalDateTime.now().minusDays(1));
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setWorkOrderId("WR013");
        workOrder2.setCompletionDateTime(LocalDateTime.now());

        // WHEN
        int result = workOrderCompletionDateTimeComparator.compare(workOrder1, workOrder2);

        // THEN
        assertTrue(result < 0);
    }

    @Test
    void compare_twoWorkOrdersWithEquivalentCompletionDateTimes_returnsCorrectComparisonValue() {
        // GIVEN
        LocalDateTime completionDateTime = LocalDateTime.now();
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setCreationDateTime(LocalDateTime.of(LocalDate.of(2023, 6, 12),
                LocalTime.of(17, 10, 5)));
        workOrder1.setCompletionDateTime(completionDateTime);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setCreationDateTime(LocalDateTime.of(LocalDate.of(2023, 6, 12),
                LocalTime.of(17, 10, 6)));
        workOrder2.setCompletionDateTime(completionDateTime);

        // WHEN
        int result = workOrderCompletionDateTimeComparator.compare(workOrder1, workOrder2);

        // THEN
        assertTrue(result < 0);
    }
}
