package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public interface EntrySignal {

    public Order getOpenOrder();

    public LazyOrder getStopOrder();

    public LazyOrder getLimitOrder();

    public PositionBehaviour getPositionBehaviour();

    public TradeableQuantilMarketRegime openMarketRegime();

    public default boolean isOpenTaker() {
        return getOpenOrder().isMarketOrder();
    }

    public default Decimal size() {
        return getOpenOrder().getSize();
    }

    public default void setSize(Decimal size) {
        getOpenOrder().setSize(size);
        getStopOrder().setSize(size);
        getLimitOrder().setSize(size);
    }

    default Pair pair() {
        return getOpenOrder().getPair();
    }

    default Decimal estimatedOpenLevel(TimeSeriesEntry currentPrice) {
        return getOpenOrder().getEstimatedExecutionLevel(currentPrice);
    }

    default Decimal estimatedStopInPoints(TimeSeriesEntry currentPrice) {
        return getOpenOrder().getEstimatedExecutionLevel(currentPrice)
                .subtract(getStopOrder().getEstimatedExecutionLevel(currentPrice))
                .abs();
    }


    default Decimal estimatedLimitInPoints(TimeSeriesEntry currentPrice) {
        return getOpenOrder().getEstimatedExecutionLevel(currentPrice)
                .subtract(getLimitOrder().getEstimatedExecutionLevel(currentPrice))
                .abs();
    }

    default Decimal estimatedLimitLevel(TimeSeriesEntry currentPrice) {
        return getLimitOrder().getEstimatedExecutionLevel(currentPrice);
    }

    default boolean isOneCloseTaker() {
        return isStopCloseTaker() || isLimitCloseTaker();
    }

    default boolean isStopCloseTaker() {
        return getStopOrder().isMarketOrder();
    }

    default boolean isLimitCloseTaker() {
        return getLimitOrder().isMarketOrder();
    }

    Decimal estimatedTargetProfit(TimeSeriesEntry currentPrice);
}
