package com.becker.freelance.backtest;

import com.becker.freelance.math.Decimal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ExcludeExistingParametersFilter implements ParameterFilter{

    private final Set<Map<String, Decimal>> existingParameters;

    public ExcludeExistingParametersFilter(Set<Map<String, Decimal>> existingParameters) {
        this.existingParameters = new HashSet<>(existingParameters);
    }

    @Override
    public Predicate<Map<String, Decimal>> getPredicate() {
        return param -> !existingParameters.contains(param);
    }

    @Override
    public void close() {
        existingParameters.clear();
    }
}
