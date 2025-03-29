package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface EntrySignal {

    public Decimal getSize();

    public Direction getDirection();

    public Pair getPair();

    public TimeSeriesEntry getOpenPrice();

    public PositionType positionType();

    public default Decimal getOpenPriceForDirection() {
        return switch (getDirection()) {
            case SELL -> getOpenPrice().closeBid();
            case BUY -> getOpenPrice().closeAsk();
        };
    }

    public default LocalDateTime getOpenTime() {
        return getOpenPrice().time();
    }

    public void visit(EntrySignalVisitor visitor);
}
