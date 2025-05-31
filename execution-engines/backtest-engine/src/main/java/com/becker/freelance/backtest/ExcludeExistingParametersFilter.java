package com.becker.freelance.backtest;

import com.becker.freelance.strategies.creation.StrategyParameter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ExcludeExistingParametersFilter implements ParameterFilter{

    private final Set<StrategyParameter> existingParameters;

    public ExcludeExistingParametersFilter(Set<StrategyParameter> existingParameters) {
        this.existingParameters = new HashSet<>(existingParameters);
    }

    @Override
    public Predicate<StrategyParameter> getPredicate() {
        return param -> !existingParameters.contains(param);
    }

    @Override
    public void close() {
        existingParameters.clear();
    }
}
