package com.example.minicapstone390.Models;

public class Sensor {
    private int sensorType;
    private double sensorData;
    private String timeStamp;

    public Sensor(){}

    public Sensor(int sensorType, double sensorData, String timeStamp) {
        this.sensorType = sensorType;
        this.sensorData = sensorData;
        this.timeStamp = timeStamp;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public double getSensorData() {
        return sensorData;
    }

    public void setSensorData(double sensorData) {
        this.sensorData = sensorData;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
