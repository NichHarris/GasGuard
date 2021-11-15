package com.example.minicapstone390.Models;

import java.util.Map;

public class Sensor {
    //TODO:
    private String id;
    private int SensorType;
    private String SensorName;
    private Map<String, Object> SensorPastValues;
    private double SensorValue;
    private boolean status;

    public Sensor() {}

    public Sensor(String id, int sensorType, String sensorName, double sensorValue) {
        this.id = id;
        this.SensorType = sensorType;
        this.SensorName = sensorName;
        this.SensorValue = sensorValue;

    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public int getSensorType() { return SensorType; }

    public void setSensorType(int sensorType) { this.SensorType = sensorType; }

    public String getSensorName() { return SensorName; }

    public void setSensorName(String sensorName) { this.SensorName = sensorName; }

    public Map<String, Object> getSensorData() { return SensorPastValues; }

    public void setSensorData(Map<String, Object> sensorData) { this.SensorPastValues = sensorData; }

    public double getSensorValue() { return SensorValue; }

    public void setSensorValue(double sensorValue) { SensorValue = sensorValue; }

    public boolean getStatus() { return status; }

    public void setStatus(Boolean status) { this.status = status; }
}

