package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.dynamodb.models.WorkOrder;

import java.util.List;

import static com.nashss.se.htmvault.utils.CollectionUtils.copyToList;

public class GetDeviceWorkOrdersResult {

    private final List<WorkOrder> workOrders;

    private GetDeviceWorkOrdersResult(List<WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    public List<WorkOrder> getWorkOrders() {
        return copyToList(workOrders);
    }

    @Override
    public String toString() {
        return "GetDeviceWorkOrdersResult{" +
                "workOrders=" + workOrders +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<WorkOrder> workOrders;

        public Builder withWorkOrders(List<WorkOrder> workOrders) {
            this.workOrders = copyToList(workOrders);
            return this;
        }
    }
}
