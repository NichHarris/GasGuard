package com.example.minicapstone390.Models;

import com.example.minicapstone390.Models.Sensor;

import java.util.Map;

public class Device {
    private String id;
    private String deviceName, location;
    private boolean status;
    private Map<String, Sensor> sensorKeys;

    public Device() {}

    public Device(String id, String deviceName, String location, boolean status) {
        this.id = id;
        this.deviceName = deviceName;
        this.location = location;
        this.status = status;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Map<String, Sensor> getSensorKeys() { return sensorKeys; }

    public void setSensorKeys(Map<String, Sensor> sensorKeys) { this.sensorKeys = sensorKeys; }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean getStatus() {
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
