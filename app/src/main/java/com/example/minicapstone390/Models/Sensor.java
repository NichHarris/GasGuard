package com.example.minicapstone390.Models;

import java.util.Map;

public class Sensor {
    private String id;
    private int SensorType;
    private String SensorName;
    private double SensorValue;
    private boolean status;
    private double SensorScore;

    public Sensor() {}

    public Sensor(String id, int sensorType, String sensorName, double sensorValue, boolean status, double sensorScore) {
        this.id = id;
        this.SensorType = sensorType;
        this.SensorName = sensorName;
        this.SensorValue = sensorValue;
        this.status = status;
        this.SensorScore = sensorScore;
    }

    public String getId() { return this.id; }

    public void setId(String id) { this.id = id; }

    public int getSensorType() { return this.SensorType; }

    public void setSensorType(int sensorType) { this.SensorType = sensorType; }

    public String getSensorName() { return this.SensorName; }

    public void setSensorName(String sensorName) { this.SensorName = sensorName; }

    public double getSensorValue() { return this.SensorValue; }

    public void setSensorValue(double sensorValue) { this.SensorValue = sensorValue; }

    public boolean getStatus() { return this.status; }

    public void setStatus(Boolean status) { this.status = status; }

    public double getSensorScore() { return this.SensorScore; }

    public void setSensorScore(double sensorScore) { this.SensorScore = sensorScore; }
}

