package com.becker.freelance.commons.position;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface Position extends Cloneable {

    public Order getOpenOrder();

    public Decimal getSize();

    public void setSize(Decimal size);

    public Direction getDirection();

    public Pair getPair();

    public Decimal getOpenPrice();

    public LocalDateTime getOpenTime();

    public Decimal getMargin();

    public LazyOrder getStopOrder();

    public LazyOrder getLimitOrder();

    public Decimal getOpenFee();

    public PositionBehaviour getPositionType();

    public boolean isOpenTaker();

    public boolean isAnyCloseTaker();

    public Position clone();

    public String getId();

    public TradeableQuantilMarketRegime getOpenMarketRegime();

    public default Decimal getLeverage() {
        return Decimal.valueOf(10);
    }

    public default Position cloneWithSize(Decimal size) {
        Position clone = clone();
        clone.setSize(size);
        return clone;
    }

    default Decimal getEstimatedLimitLevel(TimeSeriesEntry currentPrice) {
        return getLimitOrder().getEstimatedExecutionLevel(currentPrice);
    }

    default Decimal getEstimatedStopLevel(TimeSeriesEntry currentPrice) {
        return getStopOrder().getEstimatedExecutionLevel(currentPrice);
    }

    default Decimal getExecutedStopPrice() {
        return getStopOrder().executionPrice().orElseThrow(() -> new IllegalStateException("Position is not closed yet"));
    }

    default Decimal getExecutedLimitPrice() {
        return getLimitOrder().executionPrice().orElseThrow(() -> new IllegalStateException("Position is not closed yet"));
    }
}
