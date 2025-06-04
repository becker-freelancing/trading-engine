package com.becker.freelance.backtest;

import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.function.Predicate;

public interface ParameterFilter extends AutoCloseable{

    static ParameterFilter allOkFilter() {
        return new ParameterFilter() {
            @Override
            public Predicate<StrategyCreationParameter> getPredicate() {
                return map -> true;
            }

            @Override
            public void close() {

            }
        };
    }

    Predicate<StrategyCreationParameter> getPredicate();

    @Override
    void close();
}
