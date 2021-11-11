package com.example.minicapstone390.Models;

public enum GraphTimeScale {
    DAY(0),
    WEEK(7),
    WEEKS(14),
    MONTH(30),
    MONTHS(60);

    public final Integer value;

    private GraphTimeScale(Integer value) {
        this.value = value;
    }
}
