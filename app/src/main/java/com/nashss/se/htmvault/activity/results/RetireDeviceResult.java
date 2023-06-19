package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class RetireDeviceResult {

    private final DeviceModel device;

    private RetireDeviceResult(DeviceModel device) {
        this.device = device;
    }

    public DeviceModel getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "RetireDeviceResult{" +
                "device=" + device +
                '}';
    }

    //CHECKSTYLE:OFF:Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DeviceModel device;

        public Builder withDeviceModel(DeviceModel device) {
            this.device = device;
            return this;
        }

        public RetireDeviceResult build() {
            return new RetireDeviceResult(device);
        }
    }
}
