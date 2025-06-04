package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class EntrySignalFactory {


    public EntrySignal fromAmount(Decimal size,
                                  Direction direction,
                                  Decimal stopAmount,
                                  Decimal limitAmount,
                                  PositionType positionType,
                                  TimeSeriesEntry currentPrice,
                                  TradeableQuantilMarketRegime openMarketRegime) {
        return new DefaultAmountEntrySignal(
                size,
                direction,
                currentPrice.pair(),
                currentPrice,
                positionType,
                openMarketRegime,
                stopAmount,
                limitAmount
        );
    }


    public EntrySignal fromDistance(Decimal size,
                                    Direction direction,
                                    Decimal stopDistance,
                                    Decimal limitDistance,
                                    PositionType positionType,
                                    TimeSeriesEntry currentPrice,
                                    TradeableQuantilMarketRegime openMarketRegime) {
        return new DefaultDistanceEntrySignal(
                size,
                direction,
                currentPrice.pair(),
                currentPrice,
                positionType,
                openMarketRegime,
                stopDistance,
                limitDistance
        );
    }


    public EntrySignal fromLevel(Decimal size,
                                 Direction direction,
                                 Decimal stopLevel,
                                 Decimal limitLevel,
                                 PositionType positionType,
                                 TimeSeriesEntry currentPrice,
                                 TradeableQuantilMarketRegime openMarketRegime) {
        checkLevels(direction, stopLevel, limitLevel, currentPrice);
        return new DefaultLevelEntrySignal(
                size,
                direction,
                currentPrice.pair(),
                currentPrice,
                positionType,
                openMarketRegime,
                stopLevel,
                limitLevel
        );

    }

    private void checkLevels(Direction direction, Decimal stopLevel, Decimal limitLevel, TimeSeriesEntry currentPrice) {
        if (direction == Direction.BUY) {
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
        if (direction == Direction.SELL) {
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
}
