package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

final class DefaultMarketOrder implements MarketOrder {

    private final Direction direction;
    private final Pair pair;
    private final boolean reduceOnly;
    private Decimal size;
    private Decimal executionPrice;
    private LocalDateTime executionTime;

    DefaultMarketOrder(Decimal size, Direction direction, Pair pair, boolean reduceOnly) {
        if (size.isLessThanZero()) {
            throw new IllegalArgumentException("Size of Order cannot be 0 or less");
        }
        this.size = Objects.requireNonNull(size);
        this.direction = Objects.requireNonNull(direction);
        this.pair = Objects.requireNonNull(pair);
        this.reduceOnly = reduceOnly;
    }

    @Override
    public Order clone() {
        DefaultMarketOrder order = new DefaultMarketOrder(
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
        this.executionPrice = currentPrice.getCloseMid();
        this.executionTime = currentPrice.time();
    }

    @Override
    public boolean isMarketOrder() {
        return true;
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
        return true;
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
        var that = (DefaultMarketOrder) obj;
        return Objects.equals(this.size, that.size) &&
                Objects.equals(this.direction, that.direction) &&
                Objects.equals(this.pair, that.pair) &&
                this.reduceOnly == that.reduceOnly;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, direction, pair, reduceOnly);
    }

    @Override
    public String toString() {
        return "DefaultMarketOrder[" +
                "size=" + size + ", " +
                "direction=" + direction + ", " +
                "pair=" + pair + ", " +
                "reduceOnly=" + reduceOnly + ']';
    }
}
