package com.becker.freelance.backtest;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.DefaultParameterNames;
import com.becker.freelance.strategies.creation.DefaultStrategyCreationParameter;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcludeExistingParametersFilterTest {

    ExcludeExistingParametersFilter filter;
    Predicate<StrategyCreationParameter> predicate;

    @BeforeEach
    void setUp() {
        filter = new ExcludeExistingParametersFilter(Set.of(
                new DefaultStrategyCreationParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE),
                new DefaultStrategyCreationParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.TWO)
        ));
        predicate = filter.getPredicate();
    }

    @Test
    void nonExistingParameter() {
        assertTrue(predicate.test(new DefaultStrategyCreationParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.TEN)));
    }


    @Test
    void existingParameter() {
        assertFalse(predicate.test(new DefaultStrategyCreationParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE)));
    }

    @Test
    void close() {
        filter.close();
        assertTrue(predicate.test(new DefaultStrategyCreationParameter(DefaultParameterNames.TAKE_PROFIT, Decimal.ONE)));
    }

}