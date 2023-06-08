package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class UpdateDeviceResult {

    private final DeviceModel device;

    public UpdateDeviceResult(DeviceModel device) {
        this.device = device;
    }

    public DeviceModel getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "UpdateDeviceResult{" +
                "device=" + device +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DeviceModel device;

        public Builder withDeviceModel(DeviceModel device) {
            this.device = device;
            return this;
        }

        public UpdateDeviceResult build() {
            return new UpdateDeviceResult(device);
        }
    }
}
