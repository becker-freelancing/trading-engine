package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.PositionAdaptor;
import com.becker.freelance.tradeexecution.calculation.mock.PairMock;
import com.becker.freelance.tradeexecution.position.DemoPositionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrailingStopPositionTest {

    Position buyPosition;
    Position sellPosition;
    TimeSeries timeSeries;
    TimeSeriesEntry closeEntry;
    PositionAdaptor positionAdaptor;


    @BeforeEach
    void setUp() {
        timeSeries = mock(TimeSeries.class);
        closeEntry = mock(TimeSeriesEntry.class);
        positionAdaptor = new PositionAdaptor();

        when(timeSeries.getEntryForTime(any(LocalDateTime.class))).thenReturn(closeEntry);
        Mockito.doReturn(PairMock.eurUsd()).when(timeSeries).getPair();
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("1.0545"));

        TimeSeriesEntry openPrice = buildEntry(new Decimal("1.05"));
        TimeSeriesEntry openPriceSell = buildEntry(new Decimal("6100"));

        EntrySignalFactory entrySignalFactory = new EntrySignalFactory();
        DemoPositionFactory positionFactory = new DemoPositionFactory(timeSeries);
        buyPosition = positionFactory.createTrailingPosition((LevelEntrySignal) entrySignalFactory.fromLevel(Decimal.ONE, Direction.BUY, new Decimal("1.02"), new Decimal("1.08"), PositionType.TRAILING, openPrice));
        sellPosition = positionFactory.createTrailingPosition((LevelEntrySignal) entrySignalFactory.fromLevel(Decimal.ONE, Direction.SELL, new Decimal("6200"), new Decimal("6000"), PositionType.TRAILING, openPriceSell));
    }

    private static TimeSeriesEntry buildEntry(Decimal value) {
        TimeSeriesEntry price = mock(TimeSeriesEntry.class);
        when(price.closeAsk()).thenReturn(value);
        when(price.closeBid()).thenReturn(value);
        doCallRealMethod().when(price).getCloseMid();
        doReturn(LocalDateTime.of(2020, 1, 1, 0, 0)).when(price).time();
        return price;
    }

    @Test
    void adaptBuyInProfitableDirection() {
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("1.06"));
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("1.05"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("1.07"));

        List<Position> positions = List.of(buyPosition);

        positions = positionAdaptor.adapt(adapt1, positions);
        positions = positionAdaptor.adapt(adapt2, positions);
        positions = positionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("1.04"), positions.get(0).getStopLevel());
        assertEquals(new Decimal("1.08"), positions.get(0).getLimitLevel());
    }

    @Test
    void adaptBuyInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("1.03"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("1.04"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("1.024"));

        List<Position> positions = List.of(buyPosition);

        positions = positionAdaptor.adapt(adapt1, positions);
        positions = positionAdaptor.adapt(adapt2, positions);
        positions = positionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("1.02"), positions.get(0).getStopLevel());
        assertEquals(new Decimal("1.08"), positions.get(0).getLimitLevel());
    }

    @Test
    void adaptSellInProfitableDirection() {
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6090"));
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6050"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6070"));

        List<Position> positions = List.of(sellPosition);

        positions = positionAdaptor.adapt(adapt1, positions);
        positions = positionAdaptor.adapt(adapt2, positions);
        positions = positionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("6150"), positions.get(0).getStopLevel());
        assertEquals(new Decimal("6000"), positions.get(0).getLimitLevel());
    }

    @Test
    void adaptSellInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6110"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6150"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6140"));

        List<Position> positions = List.of(sellPosition);

        positions = positionAdaptor.adapt(adapt1, positions);
        positions = positionAdaptor.adapt(adapt2, positions);
        positions = positionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("6200"), positions.get(0).getStopLevel());
        assertEquals(new Decimal("6000"), positions.get(0).getLimitLevel());
    }
}