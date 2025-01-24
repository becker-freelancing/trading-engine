package com.becker.freelance.commons;


import com.becker.freelance.commons.pair.Pair;

public class TrailingStopPosition extends Position {

    private double trailingStepSize;

    public TrailingStopPosition(double size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                                double stopInPoints, double limitInPoints, double trailingStepSize, double margin) {
        super(size, direction, openPrice, pair, stopInPoints, limitInPoints, PositionType.TRAILING, margin);
        this.trailingStepSize = trailingStepSize;
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        // TODO: Implement trailing stop logic
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public double getTrailingStepSize() {
        return trailingStepSize;
    }
}
