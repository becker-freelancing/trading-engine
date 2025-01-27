package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.mock.PairMock;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarginCalculatorTest {

    Pair eurUsd;
    TimeSeries timeSeries;
    MarginCalculator marginCalculatorEurUsd;

    @BeforeEach
    void setUp() {
        eurUsd = PairMock.eurUsd();
        timeSeries = mock(TimeSeries.class);
        doReturn(eurUsd).when(timeSeries).getPair();
        marginCalculatorEurUsd = new MarginCalculator(eurUsd, timeSeries);
    }

    @Test
    void eurUsdSize1(){
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(1.0545).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(eurUsd).when(close).pair();
        doReturn(1.04).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        double margin = marginCalculatorEurUsd.calcMargin(1., close);

        assertEquals(3284.21, margin);
    }

    @Test
    void eurUsdSize0_5(){
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(1.0545).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(eurUsd).when(close).pair();
        doReturn(1.04).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        double margin = marginCalculatorEurUsd.calcMargin(0.5, close);

        assertEquals(1642.11, margin);
    }

    @Test
    void ethEurSize0_5(){
        Pair ethEur = PairMock.ethEur();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(1.0545).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(ethEur).when(close).pair();
        doReturn(3145.2).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        MarginCalculator calculator = new MarginCalculator(ethEur, timeSeries);

        double margin = calculator.calcMargin(0.5, close);
        assertEquals(786.3, margin);
    }

    @Test
    void gldUsdSize1(){
        Pair gldUsd = PairMock.gldUsd();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(0.9875).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(gldUsd).when(close).pair();
        doReturn(2600.65).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        MarginCalculator calculator = new MarginCalculator(gldUsd, timeSeries);

        double margin = calculator.calcMargin(1, close);
        assertEquals(131.68, margin);
    }

    @Test
    void gldUsdSize0_5(){
        Pair gldUsd = PairMock.gldUsd();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(1.0545).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(gldUsd).when(close).pair();
        doReturn(2600.65).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        MarginCalculator calculator = new MarginCalculator(gldUsd, timeSeries);

        double margin = calculator.calcMargin(0.5, close);
        assertEquals(61.66, margin);
    }

    @Test
    void xbtEurSize1(){
        Pair xbtEur = PairMock.xbtEur();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(0.9875).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(xbtEur).when(close).pair();
        doReturn(68752.35).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        MarginCalculator calculator = new MarginCalculator(xbtEur, timeSeries);

        double margin = calculator.calcMargin(1, close);
        assertEquals(34376.18, margin);
    }

    @Test
    void xbtEurSize0_5(){
        Pair xbtEur = PairMock.xbtEur();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(0.99425).when(entry).getCloseMid();
        doReturn(entry).when(timeSeries).getEntryForTime(any());

        TimeSeriesEntry close = mock(TimeSeriesEntry.class);
        doReturn(xbtEur).when(close).pair();
        doReturn(99362.57).when(close).getCloseMid();
        doReturn(LocalDateTime.now()).when(close).time();

        MarginCalculator calculator = new MarginCalculator(xbtEur, timeSeries);

        double margin = calculator.calcMargin(0.5, close);
        assertEquals(24840.64, margin);
    }

}