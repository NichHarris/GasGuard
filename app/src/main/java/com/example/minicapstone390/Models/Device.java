package com.example.minicapstone390.Models;

import com.example.minicapstone390.Models.Sensor;

import java.util.Map;

public class Device {
    private String deviceName;
    private boolean status;
    private Map<String, Sensor> sensors;

    public Device() {}

    public Device( String deviceName, boolean status) {
        this.deviceName = deviceName;
        this.status = status;
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
