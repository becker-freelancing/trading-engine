package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutionConfigurationTest {


    ExecutionConfiguration configuration;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Pair pair;
    TimeSeries timeSeries;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2020, 1, 1, 0, 0);
        endTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        pair = Mockito.mock(Pair.class);
        timeSeries = Mockito.mock(TimeSeries.class);
        configuration = new ExecutionConfiguration(pair, Decimal.DOUBLE_MAX, timeSeries, startTime, endTime);
    }

    @Test
    void getEurUsdTimeSeries() {
        assertEquals(timeSeries, configuration.getEurUsdTimeSeries());
    }

    @Test
    void pair() {
        assertEquals(pair, configuration.pair());
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