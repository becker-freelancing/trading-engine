package com.becker.freelance.app;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.ParameterName;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.Map;

public class DummyStrategyCreationParameter implements StrategyCreationParameter {
    @Override
    public void addParameter(ParameterName name, Decimal value) {
        throw new IllegalStateException("Not supported on dummy parameters");
    }

    @Override
    public Decimal getParameter(ParameterName parameterName) {
        throw new IllegalStateException("Not supported on dummy parameters");
    }

    @Override
    public boolean getParameterAsBool(ParameterName parameterName) {
        throw new IllegalStateException("Not supported on dummy parameters");
    }

    @Override
    public int getParameterAsInt(ParameterName parameterName) {
        throw new IllegalStateException("Not supported on dummy parameters");
    }

    @Override
    public double getParameterAsDouble(ParameterName parameterName) {
        throw new IllegalStateException("Not supported on dummy parameters");
    }

    @Override
    public Map<String, Decimal> asMap() {
        return Map.of();
    }

    @Override
    public StrategyCreationParameter clone() {
        throw new IllegalStateException("Not supported on dummy parameters");
    }
}
