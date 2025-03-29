package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.calculation.ProfitLossCalculation;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.TradingCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.mock.PairMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Position position = getPosition(new Decimal("1.04876"), PairMock.eurUsd(), Decimal.TEN, Direction.BUY);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("1.29864"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("2369653.86"), profitLoss);
        assertEquals(new Decimal("1.0545"), umrechnungsFactor);
    }

    @Test
    public void testWithEurUsdSize0_5() {
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Position position = getPosition(new Decimal("1.04876"), PairMock.eurUsd(), Decimal.ONE, Direction.SELL);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("1.29864"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("-236965.39"), profitLoss);
        assertEquals(new Decimal("1.0545"), umrechnungsFactor);
    }

    @Test
    public void testWithEthEurSize1() {
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Position position = getPosition(new Decimal("3361.1"), PairMock.ethEur(), Decimal.ONE, Direction.SELL);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("3351.5"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("96.0"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }

    @Test
    public void testWithEthEurSize0_5() {
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Position position = getPosition(new Decimal("3361.1"), PairMock.ethEur(), new Decimal("0.1"), Direction.BUY);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("3351.5"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("-9.60"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }

    @Test
    public void testWithPaxgUsdSize1() {
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("0.9875"));

        Position position = getPosition(new Decimal("2601.05"), PairMock.gldUsd(), Decimal.TEN, Direction.BUY);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("2655.2"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("5483.54"), profitLoss);
        assertEquals(new Decimal("0.9875"), umrechnungsFactor);
    }

    @Test
    public void testWithXbtEurSize1() {
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);
        LocalDateTime time = LocalDateTime.of(2020, 1, 1, 0, 0);

        Position position = getPosition(new Decimal("68752.35"), PairMock.xbtEur(), Decimal.ONE, Direction.SELL);
        ProfitLossCalculation profitLossResult = calculator.getProfitInEuro(position, new Decimal("72015.6"), time);
        Decimal profitLoss = profitLossResult.profit();
        Decimal umrechnungsFactor = profitLossResult.conversionRate();

        assertEquals(new Decimal("-32632.50"), profitLoss);
        assertEquals(Decimal.ONE, umrechnungsFactor);
    }

    Position getPosition(Decimal openPrice, Pair pair, Decimal size, Direction direction) {
        Position mock = mock(Position.class);
        doReturn(openPrice).when(mock).getOpenPrice();
        doReturn(pair).when(mock).getPair();
        doReturn(size).when(mock).getSize();
        doReturn(direction).when(mock).getDirection();
        return mock;
    }

    @Test
    void calcDistanceInEurosFromDistanceInPointsAbsoluteForEurUsd(){
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);

        Decimal distance = calculator.getDistanceByAmount(PairMock.eurUsd(), new Decimal("2"), new Decimal("5"));

        assertEquals(new Decimal("0.0000025"), distance);
    }

    @Test
    void calcDistanceInEurosFromDistanceInPointsAbsoluteForXbtEur(){
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);

        Decimal distance = calculator.getDistanceByAmount(PairMock.xbtEur(), new Decimal("1"), new Decimal("100"));

        assertEquals(new Decimal("10"), distance);
    }

    @Test
    void calcDistanceInEurosFromDistanceInPointsAbsoluteForXbtEurSize0_5(){
        TradingCalculator calculator = new TradingCalculatorImpl(timeSeries);

        Decimal distance = calculator.getDistanceByAmount(PairMock.xbtEur(), new Decimal("0.5"), new Decimal("100"));

        assertEquals(new Decimal("20"), distance);
    }
}