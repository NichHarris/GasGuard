package com.example.minicapstone390.Controllers;

public enum DatabaseEnv {
    DEVICES("Devices"),
    DEVICENAME("deviceName"),
    DEVICELOCATION("location"),
    DEVICESENSORS("sensors"),
    DEVICESTATUS("status"),
    USERS("Users"),
    USERDEVICES("devices"),
    USEREMAIL("userEmail"),
    USERFIRST("userFirstName"),
    USERLAST("userLastName"),
    USERNAME("userName"),
    USERPHONE("userPhone"),
    SENSORS("Sensors"),
    SENSORNAME("SensorName"),
    SENSORPAST("SensorPastValues"),
    VALUE("Value"),
    SENSORTYPE("SensorType"),
    SENSORVALUE("SensorValue"),
    SENSORSCORE("SensorScore"),
    SENSORSTATUS("status");

    private String endpoint;

    DatabaseEnv(String endpoint) { this.endpoint = endpoint; }

    public String getEnv() { return this.endpoint; }
}
