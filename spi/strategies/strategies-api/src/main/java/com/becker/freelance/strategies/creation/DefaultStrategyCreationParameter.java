package com.becker.freelance.strategies.creation;

import com.becker.freelance.math.Decimal;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultStrategyCreationParameter implements StrategyCreationParameter {

    private final Map<String, Decimal> parameters;

    public DefaultStrategyCreationParameter(Map<ParameterName, Decimal> parameters) {
        this.parameters = new HashMap<>(parameters.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), entry -> entry.getValue())));
    }

    public DefaultStrategyCreationParameter(ParameterName name, Decimal value) {
        this(Map.of(name, value));
    }

    private DefaultStrategyCreationParameter(DefaultStrategyCreationParameter parameter) {
        this.parameters = new HashMap<>(parameter.parameters);
    }

    @Override
    public void addParameter(ParameterName name, Decimal value) {
        parameters.put(name.getName(), value);
    }

    @Override
    public Decimal getParameter(ParameterName parameterName) {
        return parameters.get(parameterName.getName());
    }

    @Override
    public boolean getParameterAsBool(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        if (parameter.equals(Decimal.ZERO)) {
            return false;
        } else if (parameter.equals(Decimal.ONE)) {
            return true;
        }

        throw new IllegalStateException("Could not convert value " + parameter + " to boolean for parameter with name " + parameterName.getName());
    }

    @Override
    public int getParameterAsInt(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        return parameter.intValue();
    }


    @Override
    public double getParameterAsDouble(ParameterName parameterName) {
        Decimal parameter = getParameter(parameterName);
        return parameter.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefaultStrategyCreationParameter that = (DefaultStrategyCreationParameter) o;
        return Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parameters);
    }

    @Override
    public String toString() {
        return "DefaultStrategyCreationParameter{" +
                "parameters=" + parameters +
                '}';
    }

    @Override
    public Map<String, Decimal> asMap() {
        return parameters.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public StrategyCreationParameter clone() {
        return new DefaultStrategyCreationParameter(this);
    }


}
