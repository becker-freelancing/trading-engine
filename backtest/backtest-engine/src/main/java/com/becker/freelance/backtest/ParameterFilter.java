package com.becker.freelance.backtest;

import java.util.Map;
import java.util.function.Predicate;
import com.becker.freelance.math.Decimal;

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

    public Predicate<Map<String, Decimal>> getPredicate();

    @Override
    void close();
}
