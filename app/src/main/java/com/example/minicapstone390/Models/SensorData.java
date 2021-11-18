package com.example.minicapstone390.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SensorData {
    private ArrayList<Double> values;
    private ArrayList<LocalDateTime> times;

    public SensorData(ArrayList<Double> values, ArrayList<LocalDateTime> times) {
        this.values = values;
        this.times = times;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public ArrayList<LocalDateTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<LocalDateTime> times) {
        this.times = times;
    }
}
