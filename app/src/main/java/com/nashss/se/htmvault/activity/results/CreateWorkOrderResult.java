package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.WorkOrderModel;

public class CreateWorkOrderResult {

    private final WorkOrderModel workOrder;

    private CreateWorkOrderResult(WorkOrderModel workOrder) {
        this.workOrder = workOrder;
    }

    public WorkOrderModel getWorkOrder() {
        return workOrder;
    }

    @Override
    public String toString() {
        return "CreateWorkOrderResult{" +
                "workOrder=" + workOrder +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WorkOrderModel workOrder;

        public Builder withWorkOrderModel(WorkOrderModel workOrder) {
            this.workOrder = workOrder;
            return this;
        }

        public CreateWorkOrderResult build() {
            return new CreateWorkOrderResult(workOrder);
        }
    }
}
