package com.nashss.se.htmvault.dynamodb.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderCompletionDateComparatorTest {

    WorkOrderCompletionDateComparator workOrderIdComparator = new WorkOrderCompletionDateComparator();

    @Test
    void compare_nonEqualWorkOrderIds_returnsCorrectComparisonValue() {
        // GIVEN
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setWorkOrderId("1");

        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setWorkOrderId("2");

        // WHEN
        int comparison = workOrderIdComparator.compare(workOrder1, workOrder2);

        // THEN
        assertTrue(comparison < 0);
    }

    @Test

    void compare_equalWorkOrderIds_returnsCorrectComparisonValue() {
        // GIVEN
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setWorkOrderId("3");

        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setWorkOrderId("3");

        // WHEN
        int comparison = workOrderIdComparator.compare(workOrder1, workOrder2);

        // THEN
        assertEquals(0, comparison);
    }
}