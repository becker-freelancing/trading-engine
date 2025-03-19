package com.becker.freelance.backtest.configuration;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
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
    TimeSeries timeSeries;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        endTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        pair = List.of(Mockito.mock(Pair.class));
        timeSeries = Mockito.mock(TimeSeries.class);
        configuration = new BacktestExecutionConfiguration(pair, Decimal.DOUBLE_MAX, timeSeries, startTime, endTime, 10);
    }

    @Test
    void getEurUsdTimeSeries() {
        assertEquals(timeSeries, configuration.getEurUsdTimeSeries());
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
    void eurUsd() {
        assertEquals(timeSeries, configuration.eurUsd());
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