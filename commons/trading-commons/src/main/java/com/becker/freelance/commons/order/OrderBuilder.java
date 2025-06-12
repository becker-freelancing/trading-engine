package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface OrderBuilder {

    public static OrderBuilder getInstance() {
        return new BaseOrderBuilder();
    }

    public OrderBuilder withSize(Decimal size);

    public OrderBuilder withDirection(Direction direction);

    public OrderBuilder withPair(Pair pair);

    public OrderBuilder withReduceOnly(boolean reduceOnly);

    public MarketOrderBuilder asMarketOrder();

    public LimitOrderBuilder asLimitOrder();

    public ConditionalOrderBuilder asConditionalOrder();

    public Order build(LocalDateTime currentTime);

    Pair getPair();

    Decimal getSize();

    boolean isReduceOnly();

    Direction getDirection();

    Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice);
}
