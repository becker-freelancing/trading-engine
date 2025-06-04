package com.becker.freelance.backtest;

import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ExcludeExistingParametersFilter implements ParameterFilter{

    private final Set<StrategyCreationParameter> existingParameters;

    public ExcludeExistingParametersFilter(Set<StrategyCreationParameter> existingParameters) {
        this.existingParameters = new HashSet<>(existingParameters);
    }

    @Override
    public Predicate<StrategyCreationParameter> getPredicate() {
        return param -> !existingParameters.contains(param);
    }

    @Override
    public void close() {
        existingParameters.clear();
    }
}
