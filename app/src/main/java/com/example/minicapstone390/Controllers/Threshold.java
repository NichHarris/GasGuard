package com.example.minicapstone390.Controllers;

public enum Threshold {
    MQ2(0.7),
    MQ3(1.0),
    MQ4(1.0),
    MQ6(0.70),
    MQ7(0.6),
    MQ8(0.64),
    MQ9(0.62),
    MQ135(0.64);

    private double threshold;

    Threshold(Double threshold) { this.threshold = threshold; }

    public double getThreshold() { return this.threshold; }
}
