package com.becker.freelance.commons.position;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface Position extends Cloneable {

    public Decimal getSize();

    public void setSize(Decimal size);

    public Direction getDirection();

    public Pair getPair();

    public Decimal getOpenPrice();

    public LocalDateTime getOpenTime();

    public Decimal getMargin();

    public Decimal getStopLevel();

    public Decimal getLimitLevel();

    public Decimal getOpenFee();

    public PositionBehaviour getPositionType();

    public boolean isOpenTaker();

    public boolean isCloseTaker();

    public Position clone();

    public String getId();

    public TradeableQuantilMarketRegime getOpenMarketRegime();

    public default Position cloneWithSize(Decimal size) {
        Position clone = clone();
        clone.setSize(size);
        return clone;
    }
}
