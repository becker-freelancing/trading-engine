package com.becker.freelance.backtest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ExcludeExistingParametersFilter implements ParameterFilter{

    private final Set<Map<String, Double>> existingParameters;

    public ExcludeExistingParametersFilter(Set<Map<String, Double>> existingParameters) {
        this.existingParameters = new HashSet<>(existingParameters);
    }

    @Override
    public Predicate<Map<String, Double>> getPredicate() {
        return existingParameters::contains;
    }

    @Override
    public void close() {
        existingParameters.clear();
    }
}
