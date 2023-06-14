package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.WorkOrderModel;

public class UpdateWorkOrderResult {

    private WorkOrderModel workOrder;

    private UpdateWorkOrderResult(WorkOrderModel workOrder) {
        this.workOrder = workOrder;
    }

    private UpdateWorkOrderResult(Builder builder) {
        workOrder = builder.workOrder;
    }

    public WorkOrderModel getWorkOrder() {
        return workOrder;
    }

    @Override
    public String toString() {
        return "UpdateWorkOrderResult{" +
                "workOrder=" + workOrder +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WorkOrderModel workOrder;

        public Builder withWorkOrder(WorkOrderModel workOrder) {
            this.workOrder = workOrder;
            return this;
        }

        public UpdateWorkOrderResult build() {
            return new UpdateWorkOrderResult(workOrder);
        }
    }
}
