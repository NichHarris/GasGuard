package com.example.minicapstone390.Models;

import java.util.HashMap;
import java.util.Map;

public class Sensor {
    private int sensorType;
    private String sensorName;
    private Map<String, Object> sensorData;

    public Sensor() {
    }

    public Sensor(int sensorType, String sensorName) {
        this.sensorType = sensorType;
        this.sensorName = sensorName;
    }

    public int getSensorType() { return sensorType; }

    public void setSensorType(int sensorType) { this.sensorType = sensorType; }

    public String getSensorName() { return sensorName; }

    public void setSensorName(String sensorName) { this.sensorName = sensorName; }

    public Map<String, Object> getSensorData() { return sensorData; }

    public void setSensorData(Map<String, Object> sensorData) { this.sensorData = sensorData; }
}

