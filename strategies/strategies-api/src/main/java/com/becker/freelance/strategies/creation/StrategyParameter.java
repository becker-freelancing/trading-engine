package com.becker.freelance.strategies.creation;

import com.becker.freelance.math.Decimal;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StrategyParameter {

    private final Map<ParameterName, Decimal> parameters;

    public StrategyParameter(Map<ParameterName, Decimal> parameters) {
        this.parameters = new HashMap<>(parameters);
    }

    public StrategyParameter(ParameterName name, Decimal value) {
        this(Map.of(name, value));
    }

    public StrategyParameter(StrategyParameter parameter) {
        this(parameter.parameters);
    }

    public void addParameter(ParameterName name, Decimal value) {
        parameters.put(name, value);
    }

    public Decimal getParameter(ParameterName parameterName) {
        return parameters.get(parameterName);
    }

    public boolean getParameterAsBool(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        if (parameter.equals(Decimal.ZERO)) {
            return false;
        } else if (parameter.equals(Decimal.ONE)) {
            return true;
        }

        throw new IllegalStateException("Could not convert value " + parameter + " to boolean for parameter with name " + parameterName.getName());
    }

    public int getParameterAsInt(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        return parameter.intValue();
    }


    public double getParameterAsDouble(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        return parameter.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StrategyParameter that = (StrategyParameter) o;
        return Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameters);
    }

    public Map<String, Decimal> asMap() {
        return parameters.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey().getName(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
