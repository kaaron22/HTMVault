package com.nashss.se.htmvault.dynamodb.models;

import java.util.Comparator;

public class WorkOrderCompletionDateTimeComparator implements Comparator<WorkOrder> {
    @Override
    public int compare(WorkOrder o1, WorkOrder o2) {
        if (null == o1.getCompletionDateTime() && null == o2.getCompletionDateTime()) {
            return o1.getCreationDateTime().compareTo(o2.getCreationDateTime());
        }
        else if (null == o1.getCompletionDateTime()) {
            return 1;
        } else if (null == o2.getCompletionDateTime()) {
            return -1;
        } else if (o1.getCompletionDateTime().equals(o2.getCompletionDateTime())) {
            return o1.getCreationDateTime().compareTo(o2.getCreationDateTime());
        } else {
            return o1.getCompletionDateTime().compareTo(o2.getCompletionDateTime());
        }
    }

    @Override
    public Comparator<WorkOrder> reversed() {
        return Comparator.super.reversed();
    }
}
