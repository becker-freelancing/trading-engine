package com.becker.freelance.commons.position;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.mock.PairMock;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class TrailingStopPositionTest {

    TrailingStopPosition buyPosition;
    TrailingStopPosition sellPosition;
    private TimeSeries timeSeries;
    private TimeSeriesEntry closeEntry;


    @BeforeEach
    void setUp() {
        timeSeries = mock(TimeSeries.class);
        closeEntry = mock(TimeSeriesEntry.class);

        when(timeSeries.getEntryForTime(any(LocalDateTime.class))).thenReturn(closeEntry);
        doReturn(PairMock.eurUsd()).when(timeSeries).getPair();
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("1.0545"));

        TimeSeriesEntry openPrice = buildEntry(new Decimal("1.05"));
        TimeSeriesEntry openPriceSell = buildEntry(new Decimal("6100"));

        TradingCalculator calculator = new TradingCalculator(Pair.eurUsd1(), timeSeries);
        buyPosition = new TrailingStopPosition(calculator, Decimal.ONE, Direction.BUY, openPrice, Pair.eurUsd1(), new Decimal("1.02"), new Decimal("1.08"), Decimal.TEN, Decimal.TWO);
        sellPosition = new TrailingStopPosition(calculator, Decimal.ONE, Direction.SELL, openPriceSell, Pair.eurUsd1(), new Decimal("6200"), new Decimal("6000"), Decimal.TEN, Decimal.TWO);
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

        buyPosition.adapt(adapt1);
        buyPosition.adapt(adapt2);
        buyPosition.adapt(adapt3);

        assertEquals(new Decimal("1.04"), buyPosition.getStopLevel());
        assertEquals(new Decimal("1.08"), buyPosition.getLimitLevel());
    }

    @Test
    void adaptBuyInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("1.03"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("1.04"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("1.024"));

        buyPosition.adapt(adapt2);
        buyPosition.adapt(adapt1);
        buyPosition.adapt(adapt3);

        assertEquals(new Decimal("1.02"), buyPosition.getStopLevel());
        assertEquals(new Decimal("1.08"), buyPosition.getLimitLevel());
    }

    @Test
    void adaptSellInProfitableDirection() {
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6090"));
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6050"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6070"));

        sellPosition.adapt(adapt1);
        sellPosition.adapt(adapt2);
        sellPosition.adapt(adapt3);

        assertEquals(new Decimal("6150"), sellPosition.getStopLevel());
        assertEquals(new Decimal("6000"), sellPosition.getLimitLevel());
    }

    @Test
    void adaptSellInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6110"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6150"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6140"));

        sellPosition.adapt(adapt2);
        sellPosition.adapt(adapt1);
        sellPosition.adapt(adapt3);

        assertEquals(new Decimal("6200"), sellPosition.getStopLevel());
        assertEquals(new Decimal("6000"), sellPosition.getLimitLevel());
    }
}