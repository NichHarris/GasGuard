package com.example.minicapstone390.Controllers;

public enum GasType {
    MQ2GAS("Smoke, Combustible Gas"),
    MQ3GAS("Alcohol"),
    MQ4GAS("Methane, Propane, Butane"),
    MQ6GAS("Liquefied Petroleum, Butane, Propane"),
    MQ7GAS("Carbon Monoxide"),
    MQ8GAS("Hydrogen"),
    MQ9GAS("Carbon Monoxide, Methane"),
    MQ135GAS("Ammonia Sulfide, Benzene Vapor");

    private String gasType;

    GasType(String gasType) { this.gasType = gasType; }

    public String getGasType() { return this.gasType; }
}
