package com.becker.freelance.backtest;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.DefaultParameterNames;
import com.becker.freelance.strategies.creation.StrategyParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcludeExistingParametersFilterTest {

    ExcludeExistingParametersFilter filter;
    Predicate<StrategyParameter> predicate;

    @BeforeEach
    void setUp() {
        filter = new ExcludeExistingParametersFilter(Set.of(
                new StrategyParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE),
                new StrategyParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.TWO)
        ));
        predicate = filter.getPredicate();
    }

    @Test
    void nonExistingParameter() {
        assertTrue(predicate.test(new StrategyParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.TEN)));
    }


    @Test
    void existingParameter() {
        assertFalse(predicate.test(new StrategyParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE)));
    }

    @Test
    void close() {
        filter.close();
        assertTrue(predicate.test(new StrategyParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE)));
    }

}