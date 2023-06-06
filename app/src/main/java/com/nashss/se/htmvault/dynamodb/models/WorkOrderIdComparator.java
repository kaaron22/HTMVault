package com.nashss.se.htmvault.dynamodb.models;

import java.util.Comparator;

public class WorkOrderIdComparator implements Comparator<WorkOrder> {
    @Override
    public int compare(WorkOrder o1, WorkOrder o2) {
        return o1.getWorkOrderId().compareTo(o2.getWorkOrderId());
    }

    @Override
    public Comparator<WorkOrder> reversed() {
        return Comparator.super.reversed();
    }
}
