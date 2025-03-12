package com.becker.freelance.strategies;

import com.becker.freelance.math.Decimal;

public class StrategyParameter {

    private final String name;
    private final Decimal defaultValue;
    private final Decimal minValue;
    private final Decimal maxValue;
    private final Decimal stepSize;

    public StrategyParameter(String name, Decimal defaultValue, Decimal minValue, Decimal maxValue, Decimal stepSize) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
    }


    public StrategyParameter(String name, Double defaultValue, Double minValue, Double maxValue, Double stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public StrategyParameter(String name, Integer defaultValue, Double minValue, Double maxValue, Double stepSize) {
        this(name, defaultValue.doubleValue(), minValue, maxValue, stepSize);
    }


    public StrategyParameter(String name, Integer defaultValue, Integer minValue, Integer maxValue, Integer stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public StrategyParameter(String name, Integer value) {
        this(name, value, value, value, value);
    }

    public String getName() {
        return name;
    }

    public Decimal getDefaultValue() {
        return defaultValue;
    }

    public Decimal getMinValue() {
        return minValue;
    }

    public Decimal getMaxValue() {
        return maxValue;
    }

    public Decimal getStepSize() {
        return stepSize;
    }
}





