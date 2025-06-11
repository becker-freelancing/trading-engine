package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

final class DefaultLimitOrder implements LimitOrder {

    private final Direction direction;
    private final Pair pair;
    private final boolean reduceOnly;
    private Decimal orderPrice;
    private Decimal size;

    private boolean activated = false;
    private Decimal executionPrice;
    private LocalDateTime executionTime;


    DefaultLimitOrder(Decimal orderPrice, Decimal size, Direction direction, Pair pair, boolean reduceOnly) {
        if (size.isLessThanZero()) {
            throw new IllegalArgumentException("Size of Order cannot be 0 or less");
        }

        this.size = Objects.requireNonNull(size);
        this.direction = Objects.requireNonNull(direction);
        this.pair = Objects.requireNonNull(pair);
        this.reduceOnly = reduceOnly;
        setExecutionLevel(orderPrice);
    }

    @Override
    public void setExecutionLevel(Decimal level) {
        if (level.isLessThanZero()) {
            throw new IllegalArgumentException("Order Price of Limit  Order cannot be 0 or less");
        }

        this.orderPrice = level;
    }

    @Override
    public LazyOrder clone() {
        DefaultLimitOrder order = new DefaultLimitOrder(
                orderPrice,
                size,
                direction,
                pair,
                reduceOnly
        );
        order.executionTime = executionTime;
        order.executionPrice = executionPrice;
        return order;
    }

    @Override
    public boolean isExecuted() {
        return executionPrice != null;
    }

    @Override
    public Optional<Decimal> executionPrice() {
        return Optional.ofNullable(executionPrice);
    }

    @Override
    public Optional<LocalDateTime> executionTime() {
        return Optional.ofNullable(executionTime);
    }

    @Override
    public void executeIfPossible(TimeSeriesEntry currentPrice) {
        if (isExecuted()) {
            throw new IllegalStateException("Order is already executed");
        }
        if (canBeExecuted(currentPrice)) {
            this.executionPrice = orderPrice;
            this.executionTime = currentPrice.time();
        }
    }

    @Override
    public Decimal getOrderPrice() {
        return orderPrice();
    }

    @Override
    public boolean isMarketOrder() {
        return false;
    }

    @Override
    public Decimal getSize() {
        return size();
    }

    @Override
    public void setSize(Decimal size) {
        this.size = size;
    }

    @Override
    public Direction getDirection() {
        return direction();
    }

    @Override
    public Pair getPair() {
        return pair();
    }

    @Override
    public boolean isReduceOnly() {
        return reduceOnly();
    }

    @Override
    public boolean canBeExecuted(TimeSeriesEntry currentPrice) {
        if (!activated) {
            activated = isLimitPriceReached(currentPrice);
        }
        return activated;
    }

    private boolean isLimitPriceReached(TimeSeriesEntry currentPrice) {
        return switch (getDirection()) {
            case BUY -> currentPrice.getClosePriceForDirection(direction()).isLessThanOrEqualTo(getOrderPrice());
            case SELL -> currentPrice.getClosePriceForDirection(direction()).isGreaterThanOrEqualTo(getOrderPrice());
        };
    }

    public Decimal orderPrice() {
        return orderPrice;
    }

    public Decimal size() {
        return size;
    }

    public Direction direction() {
        return direction;
    }

    public Pair pair() {
        return pair;
    }

    public boolean reduceOnly() {
        return reduceOnly;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DefaultLimitOrder) obj;
        return Objects.equals(this.orderPrice, that.orderPrice) &&
                Objects.equals(this.size, that.size) &&
                Objects.equals(this.direction, that.direction) &&
                Objects.equals(this.pair, that.pair) &&
                this.reduceOnly == that.reduceOnly;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderPrice, size, direction, pair, reduceOnly);
    }

    @Override
    public String toString() {
        return "DefaultLimitOrder[" +
                "orderPrice=" + orderPrice + ", " +
                "size=" + size + ", " +
                "direction=" + direction + ", " +
                "pair=" + pair + ", " +
                "reduceOnly=" + reduceOnly + ']';
    }
}
