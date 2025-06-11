package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import static java.util.Objects.requireNonNull;

record DefaultEntrySignal(Order openOrder,
                          LazyOrder stopOrder,
                          LazyOrder limitOrder,
                          PositionBehaviour positionBehaviour,
                          TradeableQuantilMarketRegime openMarketRegime) implements EntrySignal {

    DefaultEntrySignal {
        requireNonNull(openOrder);
        requireNonNull(stopOrder);
        requireNonNull(limitOrder);
        requireNonNull(positionBehaviour);
        requireNonNull(openMarketRegime);
    }

    @Override
    public Order getOpenOrder() {
        return openOrder();
    }

    @Override
    public LazyOrder getStopOrder() {
        return stopOrder();
    }

    @Override
    public LazyOrder getLimitOrder() {
        return limitOrder();
    }

    @Override
    public PositionBehaviour getPositionBehaviour() {
        return positionBehaviour();
    }

    @Override
    public Decimal estimatedTargetProfit(TimeSeriesEntry currentPrice) {
        Decimal openLevel = openOrder().getEstimatedExecutionLevel(currentPrice);
        Decimal limitLevel = limitOrder().getEstimatedExecutionLevel(currentPrice);
        return openLevel.subtract(limitLevel).abs().multiply(size()).multiply(pair().profitPerPointForOneContract());
    }
}
