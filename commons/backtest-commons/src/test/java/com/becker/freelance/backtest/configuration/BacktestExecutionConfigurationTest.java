package com.becker.freelance.backtest.configuration;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BacktestExecutionConfigurationTest {


    BacktestExecutionConfiguration configuration;
    LocalDateTime startTime;
    LocalDateTime endTime;
    List<Pair> pair;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        endTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        pair = List.of(Mockito.mock(Pair.class));
        configuration = new BacktestExecutionConfiguration(pair, Decimal.DOUBLE_MAX, startTime, endTime, 10, Integer.MAX_VALUE, BacktestMode.TEST);
    }


    @Test
    void pair() {
        assertEquals(pair, configuration.pairs());
    }

    @Test
    void initialWalletAmount() {
        assertEquals(Decimal.DOUBLE_MAX, configuration.initialWalletAmount());
    }

    @Test
    void startTime() {
        assertEquals(startTime, configuration.startTime());
    }

    @Test
    void endTime() {
        assertEquals(endTime, configuration.endTime());
    }
}