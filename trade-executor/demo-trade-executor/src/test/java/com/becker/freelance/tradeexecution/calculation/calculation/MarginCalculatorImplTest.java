package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.mock.PairMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MarginCalculatorImplTest {

    Pair eurUsd;
    EurUsdRequestor eurUsdRequestor;
    MarginCalculator marginCalculatorEurUsd;

    @BeforeEach
    void setUp() {
        eurUsdRequestor = mock(EurUsdRequestor.class);
        marginCalculatorEurUsd = new MarginCalculatorImpl(eurUsdRequestor);
        eurUsd = PairMock.eurUsd();
    }

    @Test
    void eurUsdSize1(){
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("1.0545")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        Decimal margin = marginCalculatorEurUsd.getMarginEur(eurUsd, Decimal.ONE, new Decimal("1.04"), LocalDateTime.now());

        assertEquals(new Decimal("3284.21"), margin);
    }

    @Test
    void eurUsdSize0_5(){
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("1.0545")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        Decimal margin = marginCalculatorEurUsd.getMarginEur(eurUsd, new Decimal("0.5"), new Decimal("1.04"), LocalDateTime.now());

        assertEquals(new Decimal("1642.11"), margin);
    }

    @Test
    void ethEurSize0_5(){
        Pair ethEur = PairMock.ethEur();

        MarginCalculator calculator = new MarginCalculatorImpl(eurUsdRequestor);

        Decimal margin = calculator.getMarginEur(ethEur, new Decimal("0.5"), new Decimal("3145.2"), LocalDateTime.now());
        assertEquals(new Decimal("786.30"), margin);
    }

    @Test
    void gldUsdSize1(){
        Pair gldUsd = PairMock.gldUsd();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("0.9875")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        MarginCalculator calculator = new MarginCalculatorImpl(eurUsdRequestor);

        Decimal margin = calculator.getMarginEur(gldUsd, Decimal.ONE, new Decimal("2600.65"), LocalDateTime.now());
        assertEquals(new Decimal("131.68"), margin);
    }

    @Test
    void gldUsdSize0_5(){
        Pair gldUsd = PairMock.gldUsd();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("1.0545")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        MarginCalculator calculator = new MarginCalculatorImpl(eurUsdRequestor);

        Decimal margin = calculator.getMarginEur(gldUsd, new Decimal("0.5"), new Decimal("2600.65"), LocalDateTime.now());
        assertEquals(new Decimal("61.66"), margin);
    }

    @Test
    void xbtEurSize1(){
        Pair xbtEur = PairMock.xbtEur();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("0.9875")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        MarginCalculator calculator = new MarginCalculatorImpl(eurUsdRequestor);

        Decimal margin = calculator.getMarginEur(xbtEur, Decimal.ONE, new Decimal("68752.35"), LocalDateTime.now());
        assertEquals(new Decimal("34376.18"), margin);
    }

    @Test
    void xbtEurSize0_5(){
        Pair xbtEur = PairMock.xbtEur();
        TimeSeriesEntry entry = mock(TimeSeriesEntry.class);
        doReturn(new Decimal("0.99425")).when(entry).getCloseMid();
        doReturn(entry).when(eurUsdRequestor).getEurUsdForTime(any());

        MarginCalculator calculator = new MarginCalculatorImpl(eurUsdRequestor);

        Decimal margin = calculator.getMarginEur(xbtEur, new Decimal("0.5"), new Decimal("99362.57"), LocalDateTime.now());
        assertEquals(new Decimal("24840.64"), margin);
    }

}