package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class ReactivateDeviceResult {

    private final DeviceModel device;

    private ReactivateDeviceResult(DeviceModel device) {
        this.device = device;
    }

    public DeviceModel getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "ReactivateDeviceResult{" +
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

        public ReactivateDeviceResult build() {
            return new ReactivateDeviceResult(device);
        }
    }
}
