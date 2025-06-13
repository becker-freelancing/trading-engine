package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.LimitOrder;
import com.becker.freelance.commons.order.MarketOrder;
import com.becker.freelance.commons.order.OrderVisitor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public record ByBitExecutedOpenOrder(
        boolean marketOrder,
        Decimal size,
        Direction direction,
        Pair pair,
        Decimal actualExecutionPrice,
        LocalDateTime actualExecutionTime
) implements MarketOrder, LimitOrder {

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
        throw new UnsupportedOperationException("Order already executed");
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
        return false;
    }

    @Override
    public boolean canBeExecuted(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("Order already executed");
    }

    @Override
    public Decimal getOrderPrice() {
        return actualExecutionPrice();
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("Order already executed");
    }

    @Override
    public boolean isExecuted() {
        return true;
    }

    @Override
    public Optional<Decimal> executionPrice() {
        return Optional.of(actualExecutionPrice());
    }

    @Override
    public Optional<LocalDateTime> executionTime() {
        return Optional.of(actualExecutionTime());
    }

    @Override
    public void executeIfPossible(TimeSeriesEntry currentPrice) {

    }

    @Override
    public void setExecutionLevel(Decimal level, LocalDateTime currentTime) {

    }

    @Override
    public LazyOrder clone() {
        return new ByBitExecutedOpenOrder(
                marketOrder(),
                size(),
                direction(),
                pair(),
                actualExecutionPrice(),
                actualExecutionTime()
        );
    }

    @Override
    public void visit(OrderVisitor visitor) {
        throw new UnsupportedOperationException("Not visitable");
    }
}
