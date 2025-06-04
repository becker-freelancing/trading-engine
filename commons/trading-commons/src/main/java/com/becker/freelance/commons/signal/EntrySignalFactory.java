package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class EntrySignalFactory {


    private final TradingCalculator tradingCalculator;

    public EntrySignalFactory(TradingCalculator tradingCalculator) {
        this.tradingCalculator = tradingCalculator;
    }

    public EntrySignal fromAmount(Decimal size,
                                  Direction direction,
                                  Decimal stopAmount,
                                  Decimal limitAmount,
                                       PositionBehaviour positionBehaviour,
                                  TimeSeriesEntry currentPrice,
                                  TradeableQuantilMarketRegime openMarketRegime) {
        Decimal limitDistance = tradingCalculator.getDistanceByAmount(currentPrice.pair(), size, limitAmount);
        Decimal stopDistance = tradingCalculator.getDistanceByAmount(currentPrice.pair(), size, stopAmount);

        return fromDistance(size, direction, stopDistance, limitDistance, positionBehaviour, currentPrice, openMarketRegime);
    }


    public EntrySignal fromDistance(Decimal size,
                                    Direction direction,
                                    Decimal stopDistance,
                                    Decimal limitDistance,
                                         PositionBehaviour positionBehaviour,
                                    TimeSeriesEntry currentPrice,
                                    TradeableQuantilMarketRegime openMarketRegime) {

        Decimal closeSpread = currentPrice.getCloseSpread();
        Decimal openPriceForDirection = EntrySignal.getOpenPriceForDirection(direction, currentPrice);
        Decimal stopLevel = getStopLevel(closeSpread, stopDistance, openPriceForDirection, direction);
        Decimal limitLevel = getLimitLevel(closeSpread, limitDistance, openPriceForDirection, direction);

        return fromLevel(size, direction, stopLevel, limitLevel, positionBehaviour, currentPrice, openMarketRegime);
    }

    private Decimal getLimitLevel(Decimal closeSpread, Decimal limitDistance, Decimal openPriceForDirection, Direction direction) {
        return switch (direction) {
            case BUY -> openPriceForDirection.add(closeSpread).add(limitDistance);
            case SELL -> openPriceForDirection.subtract(closeSpread).subtract(limitDistance);
        };
    }

    private Decimal getStopLevel(Decimal closeSpread, Decimal stopDistance, Decimal openPriceForDirection, Direction direction) {
        return switch (direction) {
            case BUY -> openPriceForDirection.subtract(stopDistance).add(closeSpread);
            case SELL -> openPriceForDirection.add(stopDistance).subtract(closeSpread);
        };
    }

    public EntrySignal fromLevel(Decimal size,
                                 Direction direction,
                                 Decimal stopLevel,
                                 Decimal limitLevel,
                                      PositionBehaviour positionBehaviour,
                                 TimeSeriesEntry currentPrice,
                                 TradeableQuantilMarketRegime openMarketRegime) {
        checkLevels(direction, stopLevel, limitLevel, currentPrice);
        return new DefaultLevelEntrySignal(
                size,
                direction,
                currentPrice.pair(),
                currentPrice,
                positionBehaviour,
                openMarketRegime,
                stopLevel,
                limitLevel
        );

    }

    private void checkLevels(Direction direction, Decimal stopLevel, Decimal limitLevel, TimeSeriesEntry currentPrice) {
        if (stopLevel.isLessThan(Decimal.ZERO)) {
            throw new IllegalStateException("Stop Level must be greater than zero");
        }
        if (limitLevel.isLessThan(Decimal.ZERO)) {
            throw new IllegalStateException("Limit Level must be grater than zero");
        }
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
