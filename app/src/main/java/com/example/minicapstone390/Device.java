package com.example.minicapstone390;

import java.util.List;

public class Device {
    private String userId, deviceName;
    private boolean status;
    private List<Sensor> sensors;

    public Device() {}

    public Device(String userId, String deviceName, boolean status) {
        this.userId = userId;
        this.deviceName = deviceName;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String profileId) {
        this.userId = userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
