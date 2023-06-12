package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.utils.CollectionUtils;

import java.util.List;

public class CreateWorkOrderResult {

    private final List<WorkOrderModel> workOrders;

    private CreateWorkOrderResult(List<WorkOrderModel> workOrders) {
        this.workOrders = workOrders;
    }

    public List<WorkOrderModel> getWorkOrders() {
        return CollectionUtils.copyToList(workOrders);
    }

    @Override
    public String toString() {
        return "CreateWorkOrderResult{" +
                "workOrders=" + workOrders +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<WorkOrderModel> workOrders;

        public Builder withWorkOrderModels(List<WorkOrderModel> workOrders) {
            this.workOrders = CollectionUtils.copyToList(workOrders);
            return this;
        }

        public CreateWorkOrderResult build() {
            return new CreateWorkOrderResult(workOrders);
        }
    }
}
