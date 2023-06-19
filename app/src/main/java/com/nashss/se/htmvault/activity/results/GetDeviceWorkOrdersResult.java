package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.WorkOrderModel;

import java.util.List;

import static com.nashss.se.htmvault.utils.CollectionUtils.copyToList;

public class GetDeviceWorkOrdersResult {

    private final List<WorkOrderModel> workOrders;

    private GetDeviceWorkOrdersResult(List<WorkOrderModel> workOrders) {
        this.workOrders = workOrders;
    }

    public List<WorkOrderModel> getWorkOrders() {
        return copyToList(workOrders);
    }

    @Override
    public String toString() {
        return "GetDeviceWorkOrdersResult{" +
                "workOrders=" + workOrders +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<WorkOrderModel> workOrders;

        public Builder withWorkOrders(List<WorkOrderModel> workOrders) {
            this.workOrders = copyToList(workOrders);
            return this;
        }

        public GetDeviceWorkOrdersResult build() {
            return new GetDeviceWorkOrdersResult(workOrders);
        }
    }
}
