package com.becker.freelance.backtest;

import com.becker.freelance.strategies.creation.StrategyParameter;

import java.util.function.Predicate;

public interface ParameterFilter extends AutoCloseable{

    static ParameterFilter allOkFilter() {
        return new ParameterFilter() {
            @Override
            public Predicate<StrategyParameter> getPredicate() {
                return map -> true;
            }

            @Override
            public void close() {

            }
        };
    }

    Predicate<StrategyParameter> getPredicate();

    @Override
    void close();
}
