package com.nashss.se.htmvault.activity.results;

import com.nashss.se.htmvault.models.DeviceModel;

public class AddDeviceResult {

    private final DeviceModel deviceModel;

    private AddDeviceResult(DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    @Override
    public String toString() {
        return "AddDeviceResult{" +
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

        public AddDeviceResult build() {
            return new AddDeviceResult(deviceModel);
        }
    }

}
