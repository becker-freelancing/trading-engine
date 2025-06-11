package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class MarketOrderBuilder implements OrderBuilder {

    private Decimal size;
    private Direction direction;
    private Pair pair;
    private boolean reduceOnly;

    @Override
    public MarketOrderBuilder withSize(Decimal size) {
        this.size = size;
        return this;
    }

    @Override
    public MarketOrderBuilder withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    @Override
    public MarketOrderBuilder withPair(Pair pair) {
        this.pair = pair;
        return this;
    }

    @Override
    public MarketOrderBuilder withReduceOnly(boolean reduceOnly) {
        this.reduceOnly = reduceOnly;
        return this;
    }

    @Override
    public MarketOrderBuilder asMarketOrder() {
        return this;
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
    public Order build() {
        return new DefaultMarketOrder(size, direction, pair, reduceOnly);
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
        return currentPrice.getClosePriceForDirection(getDirection());
    }
}
