package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;
import com.nashss.se.htmvault.utils.CollectionUtils;

import java.util.List;

public class SearchDevicesResult {

    private List<DeviceModel> devices;

    private SearchDevicesResult(List<DeviceModel> devices) {
        this.devices = devices;
    }

    public List<DeviceModel> getDevices() {
        return CollectionUtils.copyToList(devices);
    }

    @Override
    public String toString() {
        return "SearchDevicesResult{" +
                "devices=" + devices +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<DeviceModel> devices;

        public Builder withDevices(List<DeviceModel> devices) {
            this.devices = CollectionUtils.copyToList(devices);
            return this;
        }

        public SearchDevicesResult build() {
            return new SearchDevicesResult(devices);
        }
    }
}
