package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface EntrySignal {

    public static Decimal getOpenPriceForDirection(Direction direction, TimeSeriesEntry openPrice) {
        return switch (direction) {
            case SELL -> openPrice.closeBid();
            case BUY -> openPrice.closeAsk();
        };
    }

    public Decimal size();

    public Direction direction();

    public Pair pair();

    public TimeSeriesEntry openPrice();

    public PositionBehaviour positionBehaviour();

    public TradeableQuantilMarketRegime openMarketRegime();

    public default Decimal getOpenPriceForDirection() {
        return getOpenPriceForDirection(direction(), openPrice());
    }

    public default LocalDateTime getOpenTime() {
        return openPrice().time();
    }


    public default boolean isOpenTaker() {
        return true;
    }

    public default boolean isCloseTaker() {
        return true;
    }

    public Decimal stopLevel();

    public Decimal limitLevel();

    public default Decimal stopInPoints() {
        return getOpenPriceForDirection().subtract(stopLevel()).abs();
    }

    public default Decimal limitInPoints() {
        return getOpenPriceForDirection().subtract(limitLevel()).abs();
    }

    default Decimal targetProfit() {
        return getOpenPriceForDirection().subtract(limitLevel()).abs().multiply(size()).multiply(pair().profitPerPointForOneContract());
    }
}
