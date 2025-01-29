package com.becker.freelance.commons.position;


import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class HardLimitPosition extends Position {

    public HardLimitPosition(Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                             Decimal stopInPoints, Decimal limitInPoints, Decimal margin) {
        super(size, direction, openPrice, pair, stopInPoints, limitInPoints, PositionType.HARD_LIMIT, margin);
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        // No adaptation needed for hard limit position
    }
}
