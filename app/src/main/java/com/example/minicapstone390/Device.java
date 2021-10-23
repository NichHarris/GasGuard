package com.example.minicapstone390;

import java.util.List;

public class Device {
    private String profileId, deviceName;
    private boolean status;
    private List<Sensor> sensors;

    public Device() {}

    public Device(String profileId, String deviceName, boolean status) {
        this.profileId = profileId;
        this.deviceName = deviceName;
        this.status = status;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
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
