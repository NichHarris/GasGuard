package com.example.minicapstone390.Models;

import java.util.HashMap;
import java.util.Map;

public class Sensor {
    private int SensorType;
    private String SensorName;
    private Map<String, Object> SensorData;
    private Boolean status;

    public Sensor() {
    }

    public Sensor(int sensorType, String sensorName) {
        this.SensorType = sensorType;
        this.SensorName = sensorName;
    }

    public int getSensorType() { return SensorType; }

    public void setSensorType(int sensorType) { this.SensorType = sensorType; }

    public String getSensorName() { return SensorName; }

    public void setSensorName(String sensorName) { this.SensorName = sensorName; }

    public Map<String, Object> getSensorData() { return SensorData; }

    public void setSensorData(Map<String, Object> sensorData) { this.SensorData = sensorData; }

    public Boolean getStatus() { return status; }

    public void setStatus(Boolean status) { this.status = status; }
}

