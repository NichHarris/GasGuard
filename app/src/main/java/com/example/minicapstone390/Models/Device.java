package com.example.minicapstone390.Models;

public class Device {
    private String id;
    private String deviceName, location;
    private boolean status;

    public Device() {}

    public Device(String id, String deviceName, String location, boolean status) {
        this.id = id;
        this.deviceName = deviceName;
        this.location = location;
        this.status = status;
    }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    public String getDeviceName() { return this.deviceName; }

    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public boolean getStatus() { return this.status; }

    public void setStatus(boolean status) { this.status = status; }

    public void setLocation(String location) { this.location = location; }

    public String getDeviceLocation() { return this.location; }

}
