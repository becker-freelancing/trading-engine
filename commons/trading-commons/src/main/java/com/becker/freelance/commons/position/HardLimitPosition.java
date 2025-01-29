package com.becker.freelance.commons.position;


import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class HardLimitPosition extends Position {

    public static Position fromDistances(Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                                  Decimal stopInPoints, Decimal limitInPoints, Decimal margin){

        Decimal limitLevel = Position.getLimitLevelFromDistance(direction, openPrice, limitInPoints);
        Decimal stopLevel = Position.getStopLevelFromDistance(direction, openPrice, stopInPoints);

        return fromLevels(size, direction, openPrice, pair, stopLevel, limitLevel, margin);
    }

    public static Position fromLevels(Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair, Decimal stopLevel, Decimal limitLevel, Decimal margin) {
        return new HardLimitPosition(size, direction, openPrice, pair, stopLevel, limitLevel, margin);
    }

    HardLimitPosition(Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                      Decimal stopLevel, Decimal limitLevel, Decimal margin) {
        super(size, direction, openPrice, pair, stopLevel, limitLevel, PositionType.HARD_LIMIT, margin);
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        // No adaptation needed for hard limit position
    }
}
