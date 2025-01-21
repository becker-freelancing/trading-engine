package com.becker.freelance.strategies;

public class StrategyParameter {

    private String name;
    private double defaultValue;
    private double minValue;
    private double maxValue;
    private double stepSize;

    public StrategyParameter(String name, double defaultValue, double minValue, double maxValue, double stepSize) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
    }

    public String getName() {
        return name;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStepSize() {
        return stepSize;
    }
}





