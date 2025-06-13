package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.order.*;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public record ByBitStopLimitConditionalOrder(
        Decimal thresholdPrice,
        Order delegate
) implements ConditionalOrder {

    @Override
    public boolean isMarketOrder() {
        return delegate.isMarketOrder();
    }

    @Override
    public Decimal getSize() {
        return delegate.getSize();
    }

    @Override
    public void setSize(Decimal size) {
        throw new UnsupportedOperationException("Order already executed");
    }

    @Override
    public Direction getDirection() {
        return delegate.getDirection();
    }

    @Override
    public Pair getPair() {
        return delegate.getPair();
    }

    @Override
    public boolean isReduceOnly() {
        return true;
    }

    @Override
    public boolean canBeExecuted(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("Order already executed");
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        throw new UnsupportedOperationException("Order already executed");
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
        return new ByBitStopLimitConditionalOrder(
                thresholdPrice(),
                delegate()
        );
    }

    @Override
    public Order getDelegateOrder() {
        return delegate();
    }

    @Override
    public TriggerDirection getTriggerDirection() {
        return switch (getDirection()) {
            case SELL -> TriggerDirection.DOWN_CROSS;
            case BUY -> TriggerDirection.UP_CROSS;
        };
    }

    @Override
    public Decimal getThresholdPrice() {
        return thresholdPrice();
    }

    @Override
    public void visit(OrderVisitor visitor) {
        throw new UnsupportedOperationException("Not visitable");
    }
}
