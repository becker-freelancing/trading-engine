package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class ConditionalOrderBuilder implements LazyOrderBuilder {

    private Decimal size;
    private Direction direction;
    private Pair pair;
    private boolean reduceOnly;
    private OrderBuilder delegate;
    private Decimal thresholdPrice;


    public ConditionalOrderBuilder withDelegate(OrderBuilder delegate) {
        this.delegate = delegate;
        return this;
    }

    public ConditionalOrderBuilder withThresholdPrice(Decimal thresholdPrice) {
        this.thresholdPrice = thresholdPrice;
        return this;
    }

    public ConditionalOrder build(LocalDateTime currentTime) {
        return new DefaultConditionalOrder(delegate.build(currentTime), thresholdPrice, currentTime);
    }

    @Override
    public ConditionalOrderBuilder withSize(Decimal size) {
        this.size = size;
        if (delegate != null) {
            this.delegate = delegate.withSize(size);
        }
        return this;
    }

    @Override
    public ConditionalOrderBuilder withDirection(Direction direction) {
        this.direction = direction;
        if (delegate != null) {
            this.delegate = delegate.withDirection(direction);
        }
        return this;
    }

    @Override
    public ConditionalOrderBuilder withPair(Pair pair) {
        this.pair = pair;

        if (delegate != null) {
            this.delegate = delegate.withPair(pair);
        }
        return this;
    }

    @Override
    public ConditionalOrderBuilder withReduceOnly(boolean reduceOnly) {
        this.reduceOnly = reduceOnly;

        if (delegate != null) {
            this.delegate = delegate.withReduceOnly(reduceOnly);
        }
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
        return this;
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
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        if (delegate instanceof MarketOrderBuilder) {
            return thresholdPrice;
        }
        return switch (direction) {
            case SELL -> thresholdPrice.max(delegate.getEstimatedExecutionLevel(currentPrice));
            case BUY -> thresholdPrice.min(delegate.getEstimatedExecutionLevel(currentPrice));
        };
    }
}
