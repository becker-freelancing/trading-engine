package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class EntrySignalFactory {


    public EntrySignal fromAmount(Decimal size, Direction direction, Decimal stopAmount, Decimal limitAmount, PositionType positionType, TimeSeriesEntry currentPrice) {
        return new AmountEntrySignal() {
            @Override
            public Decimal getStopAmount() {
                return stopAmount;
            }

            @Override
            public Decimal getLimitAmount() {
                return limitAmount;
            }

            @Override
            public Decimal getSize() {
                return size;
            }

            @Override
            public Direction getDirection() {
                return direction;
            }

            @Override
            public Pair getPair() {
                return currentPrice.pair();
            }

            @Override
            public TimeSeriesEntry getOpenPrice() {
                return currentPrice;
            }

            @Override
            public PositionType positionType() {
                return positionType;
            }
        };
    }


    public EntrySignal fromDistance(Decimal size, Direction direction, Decimal stopDistance, Decimal limitDistance, PositionType positionType, TimeSeriesEntry currentPrice) {
        return new DistanceEntrySignal() {
            @Override
            public Decimal getStopDistance() {
                return stopDistance;
            }

            @Override
            public Decimal getLimitDistance() {
                return limitDistance;
            }

            @Override
            public Decimal getSize() {
                return size;
            }

            @Override
            public Direction getDirection() {
                return direction;
            }

            @Override
            public Pair getPair() {
                return currentPrice.pair();
            }

            @Override
            public TimeSeriesEntry getOpenPrice() {
                return currentPrice;
            }

            @Override
            public PositionType positionType() {
                return positionType;
            }
        };
    }


    public EntrySignal fromLevel(Decimal size, Direction direction, Decimal stopLevel, Decimal limitLevel, PositionType positionType, TimeSeriesEntry currentPrice) {
        return new LevelEntrySignal() {
            @Override
            public Decimal getStopLevel() {
                return stopLevel;
            }

            @Override
            public Decimal getLimitLevel() {
                return limitLevel;
            }

            @Override
            public Decimal getSize() {
                return size;
            }

            @Override
            public Direction getDirection() {
                return direction;
            }

            @Override
            public Pair getPair() {
                return currentPrice.pair();
            }

            @Override
            public TimeSeriesEntry getOpenPrice() {
                return currentPrice;
            }

            @Override
            public PositionType positionType() {
                return positionType;
            }
        };

    }
}
