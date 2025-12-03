package com.becker.freelance.commons.position;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public TradeableMarketRegime getOpenMarketRegime();

    public boolean isForceClosed();

    public void forceClose(TimeSeriesEntry closePrice);

    public Optional<Decimal> forceClosePrice();

    public Optional<LocalDateTime> forceCloseTime();

    public default LocalDateTime getCloseTime() {
        return getStopOrder().executionTime().orElseGet(() -> // Either closed by Stop
                getLimitOrder().executionTime().orElseGet(() -> // Or by Limit
                        forceCloseTime().orElseThrow(() -> new IllegalStateException("Position not closed yet")))); // Or force closed by strategy
    }

    public default Decimal getClosePrice() {
        return getStopOrder().executionPrice().orElseGet(() -> // Either closed by Stop
                getLimitOrder().executionPrice().orElseGet(() -> // Or by Limit
                        forceClosePrice().orElseThrow(() -> new IllegalStateException("Position not closed yet")))); // Or force closed by strategy
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
