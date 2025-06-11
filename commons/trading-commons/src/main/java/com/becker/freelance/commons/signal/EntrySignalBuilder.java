package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.order.*;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.util.Optional;

public class EntrySignalBuilder {

    private OrderBuilder openOrderBuilder;
    private ConditionalOrderBuilder stopOrderBuilder;
    private LazyOrderBuilder limitOrderBuilder;
    private PositionBehaviour positionBehaviour;
    private TradeableQuantilMarketRegime openMarketRegime;

    public static EntrySignalBuilder getInstance() {
        return new EntrySignalBuilder();
    }

    public EntrySignalBuilder withOpenOrder(OrderBuilder openOrder) {
        this.openOrderBuilder = openOrder;
        if (stopOrderBuilder != null) {
            stopOrderBuilder.withSize(openOrder.getSize())
                    .withDirection(Optional.ofNullable(openOrder.getDirection()).map(Direction::negate).orElse(null))
                    .withPair(openOrder.getPair());
        }
        if (limitOrderBuilder != null) {
            limitOrderBuilder.withSize(openOrder.getSize())
                    .withDirection(Optional.ofNullable(openOrder.getDirection()).map(Direction::negate).orElse(null))
                    .withPair(openOrder.getPair());
        }
        return this;
    }

    public EntrySignalBuilder withStopOrder(ConditionalOrderBuilder stopOrder) {
        this.stopOrderBuilder = stopOrder;
        if (openOrderBuilder != null) {
            stopOrderBuilder.withSize(openOrderBuilder.getSize())
                    .withDirection(Optional.ofNullable(openOrderBuilder.getDirection()).map(Direction::negate).orElse(null))
                    .withPair(openOrderBuilder.getPair());
        }
        return this;
    }

    public EntrySignalBuilder withLimitOrder(LazyOrderBuilder limitOrder) {
        this.limitOrderBuilder = limitOrder;
        if (openOrderBuilder != null) {
            limitOrderBuilder.withSize(openOrderBuilder.getSize())
                    .withDirection(Optional.ofNullable(openOrderBuilder.getDirection()).map(Direction::negate).orElse(null))
                    .withPair(openOrderBuilder.getPair());
        }
        return this;
    }

    public EntrySignalBuilder withPositionBehaviour(PositionBehaviour positionBehaviour) {
        this.positionBehaviour = positionBehaviour;
        return this;
    }

    public EntrySignalBuilder withOpenMarketRegime(TradeableQuantilMarketRegime openMarketRegime) {
        this.openMarketRegime = openMarketRegime;
        return this;
    }

    public EntrySignal build() {
        Order openOrder = buildOpenOrder();
        ConditionalOrder stopOrder = buildStopOrder(openOrder);
        LazyOrder limitOrder = buildLimitOrder(openOrder);

        return new DefaultEntrySignal(
                openOrder,
                stopOrder,
                limitOrder,
                positionBehaviour,
                openMarketRegime
        );
    }

    public EntrySignal buildValidated(TimeSeriesEntry currentPrice) {
        EntrySignal entrySignal = build();
        validate(currentPrice, entrySignal);
        return entrySignal;
    }

    private LazyOrder buildLimitOrder(Order openOrder) {
        return limitOrderBuilder
                .withReduceOnly(true)
                .withDirection(openOrder.getDirection().negate())
                .withPair(openOrder.getPair())
                .withSize(openOrder.getSize())
                .build();
    }

    private ConditionalOrder buildStopOrder(Order openOrder) {
        return stopOrderBuilder
                .withReduceOnly(true)
                .withDirection(openOrder.getDirection().negate())
                .withPair(openOrder.getPair())
                .withSize(openOrder.getSize())
                .build();
    }

    private Order buildOpenOrder() {
        return openOrderBuilder.build();
    }

    private void validate(TimeSeriesEntry currentPrice, EntrySignal entrySignal) {
        Order openOrder = entrySignal.getOpenOrder();
        LazyOrder limitOrder = entrySignal.getLimitOrder();
        if (openOrder.getDirection() == limitOrder.getDirection()) {
            throw new IllegalStateException("Direction of Open and Limit Order must be opposite");
        }

        LazyOrder stopOrder = entrySignal.getStopOrder();
        if (openOrder.getDirection() == stopOrder.getDirection()) {
            throw new IllegalStateException("Direction of Open and Stop Order must be opposite");
        }
        Decimal stopLevel = stopOrder.getNearestExecutionPrice();
        Decimal limitLevel = limitOrder.getNearestExecutionPrice();
        if (stopLevel.isLessThan(Decimal.ZERO)) {
            throw new IllegalStateException("Stop Level must be greater than zero");
        }
        if (limitLevel.isLessThan(Decimal.ZERO)) {
            throw new IllegalStateException("Limit Level must be grater than zero");
        }
        if (openOrder.getDirection() == Direction.BUY) {
            if (stopLevel.isGreaterThan(limitLevel)) {
                throw new IllegalStateException("Stop Level must be less than Limit Level for BUY-Positions");
            }
            if (stopLevel.isGreaterThan(currentPrice.getCloseMid())) {
                throw new IllegalStateException("Stop Level must be less than the current price for BUY-Positions");
            }
            if (limitLevel.isLessThan(currentPrice.getCloseMid())) {
                throw new IllegalStateException("Limit Level must be greater than the current price for BUY-Positions");
            }
        }
        if (openOrder.getDirection() == Direction.SELL) {
            if (stopLevel.isLessThan(limitLevel)) {
                throw new IllegalStateException("Stop Level must be greater than Limit Level for SELL-Positions");
            }
            if (stopLevel.isLessThan(currentPrice.getCloseMid())) {
                throw new IllegalStateException("Stop Level must be greater than the current price for SELL-Positions");
            }
            if (limitLevel.isGreaterThan(currentPrice.getCloseMid())) {
                throw new IllegalStateException("Limit Level must be less than the current price for SELL-Positions");
            }
        }
    }

    public void setSize(Decimal positionSize) {
        this.openOrderBuilder = openOrderBuilder.withSize(positionSize);
        this.stopOrderBuilder = stopOrderBuilder.withSize(positionSize);
        this.limitOrderBuilder = limitOrderBuilder.withSize(positionSize);
    }

    public OrderBuilder getOpenOrderBuilder() {
        return openOrderBuilder;
    }

    public ConditionalOrderBuilder getStopOrderBuilder() {
        return stopOrderBuilder;
    }

    public LazyOrderBuilder getLimitOrderBuilder() {
        return limitOrderBuilder;
    }

    public Pair getPair() {
        return openOrderBuilder.getPair();
    }
}

