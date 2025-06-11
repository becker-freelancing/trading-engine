package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

final class DefaultConditionalOrder implements ConditionalOrder {

    private final Order delegate;
    private final TriggerDirection triggerDirection;
    private Decimal thresholdPrice;
    private boolean activated = false;


    DefaultConditionalOrder(Order delegate, TriggerDirection triggerDirection, Decimal thresholdPrice) {
        if (thresholdPrice.isLessThanZero()) {
            throw new IllegalStateException("Threshold Price of Conditional Order cannot be 0 or less");
        }
        this.delegate = delegate;
        this.triggerDirection = getDirection() == Direction.BUY ? TriggerDirection.UP_CROSS : TriggerDirection.DOWN_CROSS;
        this.thresholdPrice = thresholdPrice;
    }

    @Override
    public void setExecutionLevel(Decimal level) {
        thresholdPrice = level;
        if (delegate instanceof LazyOrder lazyOrder) {
            lazyOrder.setExecutionLevel(level);
        }
    }

    @Override
    public LazyOrder clone() {
        return new DefaultConditionalOrder(
                delegate.clone(),
                triggerDirection,
                thresholdPrice
        );
    }

    @Override
    public boolean isExecuted() {
        return delegate.isExecuted();
    }

    @Override
    public Optional<Decimal> executionPrice() {
        return delegate.executionPrice();
    }

    @Override
    public Optional<LocalDateTime> executionTime() {
        return delegate.executionTime();
    }

    @Override
    public void executeIfPossible(TimeSeriesEntry currentPrice) {
        if (!canBeExecuted(currentPrice)) {
            return;
        }
        delegate.executeIfPossible(currentPrice);
    }

    @Override
    public Order getDelegateOrder() {
        return delegate();
    }

    @Override
    public TriggerDirection getTriggerDirection() {
        return triggerDirection();
    }

    @Override
    public boolean isMarketOrder() {
        return delegate().isMarketOrder();
    }

    @Override
    public Decimal getSize() {
        return delegate().getSize();
    }

    @Override
    public void setSize(Decimal size) {
        delegate().setSize(size);
    }

    @Override
    public Direction getDirection() {
        return delegate().getDirection();
    }

    @Override
    public Pair getPair() {
        return delegate().getPair();
    }

    @Override
    public boolean isReduceOnly() {
        return delegate().isReduceOnly();
    }

    @Override
    public boolean canBeExecuted(TimeSeriesEntry currentPrice) {
        return internalCanBeExecuted(currentPrice) && delegate().canBeExecuted(currentPrice);
    }

    @Override
    public Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        if (getDelegateOrder() instanceof MarketOrder) {
            return thresholdPrice;
        }

        return switch (getDirection()) {
            case SELL -> thresholdPrice.max(delegate.getEstimatedExecutionLevel(currentPrice));
            case BUY -> thresholdPrice.min(delegate.getEstimatedExecutionLevel(currentPrice));
        };
    }

    private boolean internalCanBeExecuted(TimeSeriesEntry currentPrice) {
        if (!activated) {
            activated = isThresholdPriceTriggered(currentPrice);
        }

        return activated;
    }

    private boolean isThresholdPriceTriggered(TimeSeriesEntry currentPrice) {
        return switch (getTriggerDirection()) {
            case UP_CROSS ->
                    currentPrice.getOpenMid().isLessThan(getThresholdPrice()) && currentPrice.getHighMid().isGreaterThanOrEqualTo(getThresholdPrice());
            case DOWN_CROSS ->
                    currentPrice.getOpenMid().isGreaterThan(getThresholdPrice()) && currentPrice.getLowMid().isLessThanOrEqualTo(getThresholdPrice());
        };
    }

    @Override
    public Decimal getThresholdPrice() {
        return thresholdPrice();
    }

    public Order delegate() {
        return delegate;
    }

    public TriggerDirection triggerDirection() {
        return triggerDirection;
    }

    public Decimal thresholdPrice() {
        return thresholdPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DefaultConditionalOrder) obj;
        return Objects.equals(this.delegate, that.delegate) &&
                Objects.equals(this.triggerDirection, that.triggerDirection) &&
                Objects.equals(this.thresholdPrice, that.thresholdPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate, triggerDirection, thresholdPrice);
    }

    @Override
    public String toString() {
        return "DefaultConditionalOrder[" +
                "delegate=" + delegate + ", " +
                "triggerDirection=" + triggerDirection + ", " +
                "thresholdPrice=" + thresholdPrice + ']';
    }

}
