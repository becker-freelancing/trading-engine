package com.becker.freelance.backtest;

import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcludeExistingParametersFilterTest {

    ExcludeExistingParametersFilter filter;
    Predicate<Map<String, Decimal>> predicate;

    @BeforeEach
    void setUp() {
        filter = new ExcludeExistingParametersFilter(Set.of(
                Map.of("tp", Decimal.ONE),
                Map.of("tp", Decimal.TWO)
        ));
        predicate = filter.getPredicate();
    }

    @Test
    void nonExistingParameter() {
        assertTrue(predicate.test(Map.of("tp", Decimal.TEN)));
    }


    @Test
    void existingParameter() {
        assertFalse(predicate.test(Map.of("tp", Decimal.ONE)));
    }

    @Test
    void close() {
        filter.close();
        assertTrue(predicate.test(Map.of("tp", Decimal.ONE)));
    }

}