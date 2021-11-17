package com.example.minicapstone390.Models;

import com.example.minicapstone390.Models.Sensor;

import java.util.Map;

public class Device {
    private String deviceName, location;
    private boolean status;
    private Map<String, Sensor> sensorKeys;

    public Device() {}

    public Device(String deviceName, String location, boolean status) {
        this.deviceName = deviceName;
        this.location = location;
        this.status = status;
    }

    public Map<String, Sensor> getSensorKeys() { return sensorKeys; }

    public void setSensorKeys(Map<String, Sensor> sensorKeys) { this.sensorKeys = sensorKeys; }

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

    public void setLocation(String location) { this.location = location; }

    public String getDeviceLocation() {
        return location;
    }

}
