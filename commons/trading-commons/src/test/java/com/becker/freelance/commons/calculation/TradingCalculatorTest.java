package com.becker.freelance.commons.calculation;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.becker.freelance.commons.mock.PairMock;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TradingCalculatorTest {

    private TimeSeries timeSeries;

    private TimeSeriesEntry closeEntry;

    @BeforeEach
    public void setUp() {
        timeSeries = mock(TimeSeries.class);
        closeEntry = mock(TimeSeriesEntry.class);

        when(timeSeries.getEntryForTime(any(LocalDateTime.class))).thenReturn(closeEntry);
        doReturn(PairMock.eurUsd()).when(timeSeries).getPair();
        when(closeEntry.closeAsk()).thenReturn(1.055);
        when(closeEntry.closeBid()).thenReturn(1.054);
    }


    @Test
    public void testWithEurUsdSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.eurUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        double profitLoss = calculator.calcProfitLoss(1.04876, 1.29864, time, Direction.BUY, 10).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(1.04876, 1.29864, time, Direction.BUY, 10).conversionRate();

        assertEquals(236965.38, profitLoss, 0.1);
        assertEquals(1.0545, umrechnungsFactor, 0.1);
    }

    @Test
    public void testWithEurUsdSize0_5() {
        TradingCalculator calculator = new TradingCalculator(PairMock.eurUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        double profitLoss = calculator.calcProfitLoss(1.04876, 1.29864, time, Direction.SELL, 1).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(1.04876, 1.29864, time, Direction.SELL, 1).conversionRate();

        assertEquals(-23696.538, profitLoss, 0.1);
        assertEquals(1.0545, umrechnungsFactor, 0.1);
    }

    @Test
    public void testWithEthEurSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.ethEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        double profitLoss = calculator.calcProfitLoss(3361.1, 3351.5, time, Direction.SELL, 1).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(3361.1, 3351.5, time, Direction.SELL, 1).conversionRate();

        assertEquals(9.6, profitLoss, 0.1);
        assertEquals(1, umrechnungsFactor, 0.1);
    }

    @Test
    public void testWithEthEurSize0_5() {
        TradingCalculator calculator = new TradingCalculator(PairMock.ethEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        double profitLoss = calculator.calcProfitLoss(3361.1, 3351.5, time, Direction.BUY, 0.1).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(3361.1, 3351.5, time, Direction.BUY, 0.1).conversionRate();

        assertEquals(-0.96, profitLoss, 0.1);
        assertEquals(1, umrechnungsFactor, 0.1);
    }

    @Test
    public void testWithPaxgUsdSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.gldUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
        when(closeEntry.closeAsk()).thenReturn(0.985);
        when(closeEntry.closeBid()).thenReturn(0.99);

        double profitLoss = calculator.calcProfitLoss(2601.05, 2655.2, time, Direction.BUY, 10).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(2601.05, 2655.2, time, Direction.BUY, 10).conversionRate();

        assertEquals(548.35, profitLoss, 0.1);
        assertEquals(0.9875, umrechnungsFactor, 0.1);
    }

    @Test
    public void testWithXbtEurSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.xbtEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        double profitLoss = calculator.calcProfitLoss(68752.35, 72015.6, time, Direction.SELL, 1).profit();
        double umrechnungsFactor = calculator.calcProfitLoss(68752.35, 72015.6, time, Direction.SELL, 1).conversionRate();

        assertEquals(-3263.25, profitLoss, 0.1);
        assertEquals(1, umrechnungsFactor, 0.1);
    }
}