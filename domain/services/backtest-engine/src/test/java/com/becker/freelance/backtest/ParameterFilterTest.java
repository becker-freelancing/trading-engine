package com.becker.freelance.backtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterFilterTest {

    ParameterFilter allOkFilter;

    @BeforeEach
    void setUp() {
        allOkFilter = ParameterFilter.allOkFilter();
    }

    @Test
    void ok() {
        assertTrue(allOkFilter.getPredicate().test(null));
    }

    @Test
    void close() {
        allOkFilter.close();
        assertTrue(allOkFilter.getPredicate().test(null));
    }
}