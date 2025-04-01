package com.becker.freelance.bybit.trades;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.position.StopLimitPosition;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

class BybitPosition implements StopLimitPosition {

    private final PositionResponse position;

    public BybitPosition(PositionResponse positionResponse) {
        this.position = positionResponse;
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
    public Decimal getStopLevel() {
        return position.stopLevel();
    }

    @Override
    public Decimal getLimitLevel() {
        return position.limitLevel();
    }

    @Override
    public PositionType getPositionType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Position clone() {
        return new BybitPosition(position);
    }

    @Override
    public String getId() {
        return position.id();
    }
}
