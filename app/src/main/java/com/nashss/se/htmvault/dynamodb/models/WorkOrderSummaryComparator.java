package com.nashss.se.htmvault.dynamodb.models;

import java.util.Comparator;

public class WorkOrderSummaryComparator implements Comparator<WorkOrderSummary> {
    @Override
    public int compare(WorkOrderSummary o1, WorkOrderSummary o2) {
        return o1.getWorkOrderId().compareTo(o2.getWorkOrderId());
    }

    @Override
    public Comparator<WorkOrderSummary> reversed() {
        return Comparator.super.reversed();
    }
}
