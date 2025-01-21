package com.becker.freelance.commons;


public class HardLimitPosition extends Position {

    public HardLimitPosition(double size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                             double stopInPoints, double limitInPoints, double margin) {
        super(size, direction, openPrice, pair, stopInPoints, limitInPoints, PositionType.HARD_LIMIT, margin);
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        // No adaptation needed for hard limit position
    }
}
