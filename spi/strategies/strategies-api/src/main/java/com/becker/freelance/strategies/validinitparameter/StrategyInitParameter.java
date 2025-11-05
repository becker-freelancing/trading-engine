package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.ParameterName;

public class StrategyInitParameter {

    private final ParameterName name;
    private final Decimal defaultValue;
    private final Decimal minValue;
    private final Decimal maxValue;
    private final Decimal stepSize;

    public StrategyInitParameter(ParameterName name, Decimal defaultValue, Decimal minValue, Decimal maxValue, Decimal stepSize) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
    }


    public StrategyInitParameter(ParameterName name, Double defaultValue, Double minValue, Double maxValue, Double stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public StrategyInitParameter(ParameterName name, Integer defaultValue, Double minValue, Double maxValue, Double stepSize) {
        this(name, defaultValue.doubleValue(), minValue, maxValue, stepSize);
    }


    public StrategyInitParameter(ParameterName name, Integer defaultValue, Integer minValue, Integer maxValue, Integer stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public StrategyInitParameter(ParameterName name, Integer value) {
        this(name, value, value, value, value);
    }

    public ParameterName getName() {
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





