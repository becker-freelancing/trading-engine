package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.order.LazyOrder;
import com.becker.freelance.commons.order.Order;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.position.StopLimitPosition;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.UUID;

public class DemoStopLimitPosition implements StopLimitPosition {

    private final Order openOrder;
    private final LazyOrder stopOrder;
    private final LazyOrder limitOrder;
    private final MarginCalculator marginCalculator;
    private final TradingFeeCalculator tradingFeeCalculator;
    private final String id;
    private final TradeableQuantilMarketRegime openMarketRegime;

    public DemoStopLimitPosition(TradeableQuantilMarketRegime openMarketRegime, TradingFeeCalculator tradingFeeCalculator, MarginCalculator marginCalculator, LazyOrder limitOrder, LazyOrder stopOrder, Order openOrder) {
        this(openMarketRegime, tradingFeeCalculator, marginCalculator, limitOrder, stopOrder, openOrder, UUID.randomUUID().toString());
    }

    public DemoStopLimitPosition(TradeableQuantilMarketRegime openMarketRegime, TradingFeeCalculator tradingFeeCalculator, MarginCalculator marginCalculator, LazyOrder limitOrder, LazyOrder stopOrder, Order openOrder, String id) {
        this.openOrder = openOrder;
        this.stopOrder = stopOrder;
        this.limitOrder = limitOrder;
        this.marginCalculator = marginCalculator;
        this.tradingFeeCalculator = tradingFeeCalculator;
        this.id = id;
        this.openMarketRegime = openMarketRegime;
    }

    @Override
    public Decimal getSize() {
        return openOrder.getSize();
    }

    @Override
    public void setSize(Decimal size) {
        openOrder.setSize(size);
        stopOrder.setSize(size);
        limitOrder.setSize(size);
    }

    @Override
    public Direction getDirection() {
        return openOrder.getDirection();
    }

    @Override
    public Pair getPair() {
        return openOrder.getPair();
    }

    @Override
    public Decimal getOpenPrice() {
        return openOrder.executionPrice().orElseThrow(() -> new IllegalStateException("Open Order has not been executed yet"));
    }

    @Override
    public LocalDateTime getOpenTime() {
        return openOrder.executionTime().orElseThrow(() -> new IllegalStateException("Open Order has not been executed yet"));
    }

    @Override
    public Decimal getMargin() {
        return marginCalculator.getMarginEurWithLeverage(getPair(), getSize(), getOpenPrice(), getOpenTime(), getLeverage());
    }

    @Override
    public LazyOrder getStopOrder() {
        return stopOrder;
    }

    @Override
    public LazyOrder getLimitOrder() {
        return limitOrder;
    }

    @Override
    public Decimal getOpenFee() {
        Decimal openPrice = getOpenPrice();
        if (openOrder.isMarketOrder()) {
            return tradingFeeCalculator.calculateTakerTradingFeeInCounterCurrency(openPrice, getSize());
        }

        return tradingFeeCalculator.calculateMakerTradingFeeInCounterCurrency(openPrice, getSize());
    }

    @Override
    public PositionBehaviour getPositionType() {
        return PositionBehaviour.HARD_LIMIT;
    }

    @Override
    public boolean isOpenTaker() {
        return openOrder.isMarketOrder();
    }

    @Override
    public boolean isAnyCloseTaker() {
        return stopOrder.isMarketOrder() || limitOrder.isMarketOrder();
    }

    @Override
    public Position clone() {
        return new DemoStopLimitPosition(openMarketRegime, tradingFeeCalculator, marginCalculator,
                limitOrder.clone(),
                stopOrder.clone(),
                openOrder.clone(),
                id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TradeableQuantilMarketRegime getOpenMarketRegime() {
        return openMarketRegime;
    }

    @Override
    public Order getOpenOrder() {
        return openOrder;
    }
}
