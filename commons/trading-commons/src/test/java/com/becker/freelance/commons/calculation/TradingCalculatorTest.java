package com.becker.freelance.commons.calculation;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.becker.freelance.commons.mock.PairMock;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
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
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("1.0545"));
    }


    @Test
    public void testWithEurUsdSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.eurUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("1.04876"), new Decimal("1.29864"), time, Direction.BUY, Decimal.TEN).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("1.04876"), new Decimal("1.29864"), time, Direction.BUY, Decimal.TEN).conversionRate();

        assertEquals(new Decimal("236965.38"), profitLoss);
        assertEquals(new Decimal("1.0545"), umrechnungsFactor);
    }

    @Test
    public void testWithEurUsdSize0_5() {
        TradingCalculator calculator = new TradingCalculator(PairMock.eurUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("1.04876"), new Decimal("1.29864"), time, Direction.SELL, Decimal.ONE).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("1.04876"), new Decimal("1.29864"), time, Direction.SELL, Decimal.ONE).conversionRate();

        assertEquals(new Decimal("-23696.538"), profitLoss);
        assertEquals(new Decimal("1.0545"), umrechnungsFactor);
    }

    @Test
    public void testWithEthEurSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.ethEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("3361.1"), new Decimal("3351.5"), time, Direction.SELL, Decimal.ONE).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("3361.1"), new Decimal("3351.5"), time, Direction.SELL, Decimal.ONE).conversionRate();

        assertEquals(new Decimal("9.6"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }

    @Test
    public void testWithEthEurSize0_5() {
        TradingCalculator calculator = new TradingCalculator(PairMock.ethEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("3361.1"), new Decimal("3351.5"), time, Direction.BUY, new Decimal("0.1")).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("3361.1"), new Decimal("3351.5"), time, Direction.BUY, new Decimal("0.1")).conversionRate();

        assertEquals(new Decimal("-0.96"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }

    @Test
    public void testWithPaxgUsdSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.gldUsd(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("0.9875"));

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("2601.05"), new Decimal("2655.2"), time, Direction.BUY, Decimal.TEN).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("2601.05"), new Decimal("2655.2"), time, Direction.BUY, Decimal.TEN).conversionRate();

        assertEquals(new Decimal("548.35"), profitLoss);
        assertEquals(new Decimal("0.9875"), umrechnungsFactor);
    }

    @Test
    public void testWithXbtEurSize1() {
        TradingCalculator calculator = new TradingCalculator(PairMock.xbtEur(), timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Decimal profitLoss = calculator.calcProfitLoss(new Decimal("68752.35"), new Decimal("72015.6"), time, Direction.SELL, Decimal.ONE).profit();
        Decimal umrechnungsFactor = calculator.calcProfitLoss(new Decimal("68752.35"), new Decimal("72015.6"), time, Direction.SELL, Decimal.ONE).conversionRate();

        assertEquals(new Decimal("-3263.25"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }
}