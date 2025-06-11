package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class LimitOrderBuilder implements LazyOrderBuilder {

    private Decimal size;
    private Direction direction;
    private Pair pair;
    private boolean reduceOnly;
    private Decimal orderPrice;

    public LimitOrderBuilder withOrderPrice(Decimal orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public LimitOrder build() {
        return new DefaultLimitOrder(orderPrice, size, direction, pair, reduceOnly);
    }

    @Override
    public LimitOrderBuilder withSize(Decimal size) {
        this.size = size;
        return this;
    }

    @Override
    public LimitOrderBuilder withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public LimitOrderBuilder withPair(Pair pair) {
        this.pair = pair;
        return this;
    }

    @Override
    public LimitOrderBuilder withReduceOnly(boolean reduceOnly) {
        this.reduceOnly = reduceOnly;
        return this;
    }

    @Override
    public MarketOrderBuilder asMarketOrder() {
        return new MarketOrderBuilder()
                .withSize(size)
                .withDirection(direction)
                .withPair(pair)
                .withReduceOnly(reduceOnly);
    }

    @Override
    public LimitOrderBuilder asLimitOrder() {
        return this;
    }

    @Override
    public ConditionalOrderBuilder asConditionalOrder() {
        return new ConditionalOrderBuilder()
                .withSize(size)
                .withDirection(direction)
                .withPair(pair)
                .withReduceOnly(reduceOnly);
    }

    @Override
    public Decimal getSize() {
        return size;
    }

    @Override
    public boolean isReduceOnly() {
        return reduceOnly;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Pair getPair() {
        return pair;
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        return orderPrice;
    }
}
