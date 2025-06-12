package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

class BaseOrderBuilder implements OrderBuilder {

    private Decimal size;
    private Direction direction;
    private Pair pair;
    private boolean reduceOnly = false;

    public BaseOrderBuilder withSize(Decimal size) {
        if (size.isLessThanOrEqualTo(Decimal.ZERO)) {
            throw new IllegalStateException("Size must be greater than 0");
        }
        this.size = size;
        return this;
    }

    public BaseOrderBuilder withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public BaseOrderBuilder withPair(Pair pair) {
        this.pair = pair;
        return this;
    }

    public BaseOrderBuilder withReduceOnly(boolean reduceOnly) {
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
        return new LimitOrderBuilder()
                .withSize(size)
                .withDirection(direction)
                .withPair(pair)
                .withReduceOnly(reduceOnly);
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
    public Order build(LocalDateTime currentTime) {
        throw new UnsupportedOperationException("One Concrete Order Type must be selected");
    }

    @Override
    public Pair getPair() {
        return pair;
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
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("One Concrete Order Type must be selected");
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
