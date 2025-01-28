package com.becker.freelance.backtest;

import java.util.Map;
import java.util.function.Predicate;

public interface ParameterFilter extends AutoCloseable{

    static ParameterFilter allOkFilter() {
        return new ParameterFilter() {
            @Override
            public Predicate<Map<String, Double>> getPredicate() {
                return map -> true;
            }

            @Override
            public void close() {

            }
        };
    }

    public Predicate<Map<String, Double>> getPredicate();

    @Override
    void close();
}
