package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class GetDeviceResult {

    private final DeviceModel device;

    private GetDeviceResult(DeviceModel device) {
        this.device = device;
    }

    public DeviceModel getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "GetDeviceResult{" +
                "deviceModel=" + device +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
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
