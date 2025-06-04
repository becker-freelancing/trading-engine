package com.becker.freelance.strategies.creation;

import com.becker.freelance.math.Decimal;

import java.util.Map;

public interface StrategyCreationParameter extends Cloneable {
    void addParameter(ParameterName name, Decimal value);

    Decimal getParameter(ParameterName parameterName);

    boolean getParameterAsBool(ParameterName parameterName);

    int getParameterAsInt(ParameterName parameterName);

    double getParameterAsDouble(ParameterName parameterName);

    Map<String, Decimal> asMap();

    StrategyCreationParameter clone();
}
