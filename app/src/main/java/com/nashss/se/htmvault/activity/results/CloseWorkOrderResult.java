package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.WorkOrderModel;

public class CloseWorkOrderResult {

    private final WorkOrderModel workOrder;

    private CloseWorkOrderResult(WorkOrderModel workOrder) {
        this.workOrder = workOrder;
    }

    public WorkOrderModel getWorkOrder() {
        return workOrder;
    }

    @Override
    public String toString() {
        return "CloseWorkOrderResult{" +
                "workOrder=" + workOrder +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WorkOrderModel workOrder;

        public Builder withWorkOrderModel(WorkOrderModel workOrder) {
            this.workOrder = workOrder;
            return this;
        }

        public CloseWorkOrderResult build() {
            return new CloseWorkOrderResult(workOrder);
        }
    }
}
