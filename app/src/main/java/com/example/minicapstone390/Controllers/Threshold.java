package com.example.minicapstone390.Controllers;

public enum Threshold {
    MQ2(100.0),
    MQ3(100.0),
    MQ4(100.0),
    MQ6(100.0),
    MQ7(100.0),
    MQ8(100.0),
    MQ9(100.0),
    MQ135(100.0);

    private double threshold;

    Threshold(Double threshold) { this.threshold = threshold; }

    public double getThreshold() { return this.threshold; }
}
