package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.order.TriggerDirection;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.TrailingPositionAdaptor;
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
    TrailingPositionAdaptor trailingPositionAdaptor;


    @BeforeEach
    void setUp() {
        timeSeries = mock(TimeSeries.class);
        closeEntry = mock(TimeSeriesEntry.class);
        trailingPositionAdaptor = new TrailingPositionAdaptor();

        when(timeSeries.getEntryForTime(any(LocalDateTime.class))).thenReturn(closeEntry);
        Mockito.doReturn(PairMock.eurUsd()).when(timeSeries).getPair();
        when(closeEntry.getCloseMid()).thenReturn(new Decimal("1.0545"));

        TimeSeriesEntry openPrice = buildEntry(new Decimal("1.05"));
        TimeSeriesEntry openPriceSell = buildEntry(new Decimal("6100"));

        DemoPositionFactory positionFactory = new DemoPositionFactory(time -> closeEntry, mock(TradingFeeCalculator.class), mock(MarginCalculator.class));
        buyPosition = positionFactory.createTrailingPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.BUY).withPair(PairMock.eurUsd()).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.02"))).withThresholdPrice(new Decimal("1.02")).withTriggerDirection(TriggerDirection.DOWN_CROSS))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.08")))
                .withPositionBehaviour(PositionBehaviour.TRAILING)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .buildValidated(openPrice), openPrice);
        buyPosition.getOpenOrder().executeIfPossible(openPrice);

        sellPosition = positionFactory.createTrailingPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.SELL).withPair(PairMock.eurUsd()).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("6200"))).withThresholdPrice(new Decimal("6200")).withTriggerDirection(TriggerDirection.UP_CROSS))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("6000")))
                .withPositionBehaviour(PositionBehaviour.TRAILING)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .buildValidated(openPriceSell), openPriceSell);
        sellPosition.getOpenOrder().executeIfPossible(openPriceSell);
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

        positions = trailingPositionAdaptor.adapt(adapt1, positions);
        positions = trailingPositionAdaptor.adapt(adapt2, positions);
        positions = trailingPositionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("1.04"), positions.get(0).getEstimatedStopLevel(null));
        assertEquals(new Decimal("1.08"), positions.get(0).getEstimatedLimitLevel(null));
    }

    @Test
    void adaptBuyInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("1.03"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("1.04"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("1.024"));

        List<Position> positions = List.of(buyPosition);

        positions = trailingPositionAdaptor.adapt(adapt1, positions);
        positions = trailingPositionAdaptor.adapt(adapt2, positions);
        positions = trailingPositionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("1.02"), positions.get(0).getEstimatedStopLevel(null));
        assertEquals(new Decimal("1.08"), positions.get(0).getEstimatedLimitLevel(null));
    }

    @Test
    void adaptSellInProfitableDirection() {
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6090"));
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6050"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6070"));

        List<Position> positions = List.of(sellPosition);

        positions = trailingPositionAdaptor.adapt(adapt1, positions);
        positions = trailingPositionAdaptor.adapt(adapt2, positions);
        positions = trailingPositionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("6150"), positions.get(0).getEstimatedStopLevel(null));
        assertEquals(new Decimal("6000"), positions.get(0).getEstimatedLimitLevel(null));
    }

    @Test
    void adaptSellInUnprofitableDirection() {
        TimeSeriesEntry adapt2 = buildEntry(new Decimal("6110"));
        TimeSeriesEntry adapt1 = buildEntry(new Decimal("6150"));
        TimeSeriesEntry adapt3 = buildEntry(new Decimal("6140"));

        List<Position> positions = List.of(sellPosition);

        positions = trailingPositionAdaptor.adapt(adapt1, positions);
        positions = trailingPositionAdaptor.adapt(adapt2, positions);
        positions = trailingPositionAdaptor.adapt(adapt3, positions);

        assertEquals(new Decimal("6200"), positions.get(0).getEstimatedStopLevel(null));
        assertEquals(new Decimal("6000"), positions.get(0).getEstimatedLimitLevel(null));
    }
}