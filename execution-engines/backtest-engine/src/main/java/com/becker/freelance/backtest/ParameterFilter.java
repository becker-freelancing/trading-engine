package com.becker.freelance.backtest;

import com.becker.freelance.math.Decimal;

import java.util.Map;
import java.util.function.Predicate;

public interface ParameterFilter extends AutoCloseable{

    static ParameterFilter allOkFilter() {
        return new ParameterFilter() {
            @Override
            public Predicate<Map<String, Decimal>> getPredicate() {
                return map -> true;
            }

            @Override
            public void close() {

            }
        };
    }

    Predicate<Map<String, Decimal>> getPredicate();

    @Override
    void close();
}
