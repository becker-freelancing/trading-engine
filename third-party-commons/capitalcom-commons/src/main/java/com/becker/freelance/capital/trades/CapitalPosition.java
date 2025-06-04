package com.becker.freelance.capital.trades;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.position.StopLimitPosition;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

class CapitalPosition implements StopLimitPosition {

    private final Pair pair;
    private final PositionResponse position;

    public CapitalPosition(Pair pair, PositionResponse positionResponse) {
        this.pair = pair;
        this.position = positionResponse;
    }


    @Override
    public Decimal getSize() {
        return new Decimal(position.getSize());
    }

    @Override
    public void setSize(Decimal size) {
        throw new UnsupportedOperationException("Size is not settable on Capital Remote position yet.");
    }

    @Override
    public Direction getDirection() {
        return position.getDirection().equals("BUY") ? Direction.BUY : Direction.SELL;
    }

    @Override
    public Pair getPair() {
        return pair;
    }

    @Override
    public Decimal getOpenPrice() {
        return new Decimal(position.getLevel());
    }

    @Override
    public LocalDateTime getOpenTime() {
        return LocalDateTime.parse(position.getCreatedDate());
    }

    @Override
    public Decimal getMargin() {
        return getOpenPrice().multiply(getSize()).divide(new Decimal(position.getLeverage()));
    }

    @Override
    public Decimal getStopLevel() {
        throw new UnsupportedOperationException("Stop Level is not requestable on Capital Remote position yet.");
    }

    @Override
    public Decimal getLimitLevel() {
        throw new UnsupportedOperationException("Limit Level is not requestable on Capital Remote position yet.");
    }

    @Override
    public Decimal getOpenFee() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PositionBehaviour getPositionType() {
        throw new UnsupportedOperationException("PositionType is not requestable on Capital Remote position yet.");
    }

    @Override
    public boolean isOpenTaker() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isCloseTaker() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Position clone() {
        return new CapitalPosition(pair, position);
    }

    @Override
    public String getId() {
        return position.getDealId();
    }

    @Override
    public TradeableQuantilMarketRegime getOpenMarketRegime() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
