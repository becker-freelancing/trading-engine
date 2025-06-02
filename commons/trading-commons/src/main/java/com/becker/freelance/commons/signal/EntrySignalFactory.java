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
}
