package com.becker.freelance.bybit.trades;

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

class BybitPosition implements StopLimitPosition {

    private final PositionResponse position;
    private final Order openOrder;
    private final LazyOrder stopOrder;
    private final LazyOrder limitOrder;

    public BybitPosition(PositionResponse positionResponse) {
        this.position = positionResponse;
        this.openOrder = new ByBitExecutedOpenOrder(
                positionResponse.isMarket(),
                positionResponse.size(),
                positionResponse.direction(),
                positionResponse.pair(),
                positionResponse.openPrice(),
                positionResponse.openTime()
        );
        this.stopOrder = new ByBitStopLimitLimitOrder(
                positionResponse.isStopMarket(),
                positionResponse.size(),
                positionResponse.direction().negate(),
                positionResponse.pair(),
                positionResponse.stopLevel()
        );
        this.limitOrder = new ByBitStopLimitLimitOrder(
                positionResponse.isLimitMarket(),
                positionResponse.size(),
                positionResponse.direction().negate(),
                positionResponse.pair(),
                positionResponse.limitLevel()
        );
    }


    @Override
    public Order getOpenOrder() {
        return openOrder;
    }

    @Override
    public Decimal getSize() {
        return position.size();
    }

    @Override
    public void setSize(Decimal size) {
        throw new UnsupportedOperationException("Size is not settable on Capital Remote position yet.");
    }

    @Override
    public Direction getDirection() {
        return position.direction();
    }

    @Override
    public Pair getPair() {
        return position.pair();
    }

    @Override
    public Decimal getOpenPrice() {
        return position.openPrice();
    }

    @Override
    public LocalDateTime getOpenTime() {
        return position.openTime();
    }

    @Override
    public Decimal getMargin() {
        return position.margin();
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
        throw new UnsupportedOperationException("Not implemented yet");
    }


    @Override
    public PositionBehaviour getPositionType() {
        return position.positionType();
    }

    @Override
    public boolean isOpenTaker() {
        return getOpenOrder().isMarketOrder();
    }

    @Override
    public boolean isAnyCloseTaker() {
        return getStopOrder().isMarketOrder() || getLimitOrder().isMarketOrder();
    }

    @Override
    public Position clone() {
        return new BybitPosition(position);
    }

    @Override
    public String getId() {
        return position.id();
    }

    @Override
    public TradeableQuantilMarketRegime getOpenMarketRegime() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
