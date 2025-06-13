package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.OrderVisitor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public record ByBitStopLimitLimitOrder(
        boolean marketOrder,
        Decimal size,
        Direction direction,
        Pair pair,
        Decimal expectedExecutionPrice
) implements LimitOrder {

    @Override
    public boolean isMarketOrder() {
        return marketOrder();
    }

    @Override
    public Decimal getSize() {
        return size();
    }

    @Override
    public void setSize(Decimal size) {
        throw new UnsupportedOperationException("Managed by ByBit");
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
        return true;
    }

    @Override
    public boolean canBeExecuted(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("Managed by ByBit");
    }

    @Override
    public Decimal getOrderPrice() {
        return expectedExecutionPrice();
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        return expectedExecutionPrice();
    }

    @Override
    public boolean isExecuted() {
        return false;
    }

    @Override
    public Optional<Decimal> executionPrice() {
        return Optional.empty();
    }

    @Override
    public Optional<LocalDateTime> executionTime() {
        return Optional.empty();
    }

    @Override
    public void executeIfPossible(TimeSeriesEntry currentPrice) {

    }

    @Override
    public void setExecutionLevel(Decimal level, LocalDateTime currentTime) {

    }

    @Override
    public LazyOrder clone() {
        return new ByBitStopLimitLimitOrder(
                marketOrder(),
                size(),
                direction(),
                pair(),
                expectedExecutionPrice()
        );
    }

    @Override
    public void visit(OrderVisitor visitor) {
        throw new UnsupportedOperationException("Not visitable");
    }
}
