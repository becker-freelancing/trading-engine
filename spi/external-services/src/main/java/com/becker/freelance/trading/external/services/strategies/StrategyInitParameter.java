package com.becker.freelance.trading.external.services.strategies;

import com.becker.freelance.math.Decimal;

public record StrategyInitParameter(ParameterName name, Decimal defaultValue, Decimal minValue, Decimal maxValue,
                                    Decimal stepSize) {


    public StrategyInitParameter(ParameterName name, Integer defaultValue, Integer minValue, Integer maxValue, Integer stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public StrategyInitParameter(ParameterName name, String defaultValue, String minValue, String maxValue, String stepSize) {
        this(name, new Decimal(defaultValue), new Decimal(minValue), new Decimal(maxValue), new Decimal(stepSize));
    }

    public int getSize() {
        return maxValue.subtract(minValue).divide(stepSize).round(0).intValue();
    }
}





