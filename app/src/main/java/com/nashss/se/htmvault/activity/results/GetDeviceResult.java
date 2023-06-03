package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class GetDeviceResult {

    private final DeviceModel deviceModel;

    private GetDeviceResult(DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    @Override
    public String toString() {
        return "GetDeviceResult{" +
                "deviceModel=" + deviceModel +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DeviceModel deviceModel;

        public Builder withDeviceModel(DeviceModel deviceModel) {
            this.deviceModel = deviceModel;
            return this;
        }

        public GetDeviceResult build() {
            return new GetDeviceResult(deviceModel);
        }
    }
}
