package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class AddDeviceResult {

    private final DeviceModel device;

    private AddDeviceResult(DeviceModel device) {
        this.device = device;
    }

    public DeviceModel getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "AddDeviceResult{" +
                "deviceModel=" + device +
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

        public AddDeviceResult build() {
            return new AddDeviceResult(deviceModel);
        }
    }

}
