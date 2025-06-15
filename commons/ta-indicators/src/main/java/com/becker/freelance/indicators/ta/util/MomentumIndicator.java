package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class MomentumIndicator implements Indicator<Num> {

    private final Indicator<Num> baseIndicator;
    private final Indicator<Num> subtractIndicator;

    public MomentumIndicator(Indicator<Num> baseIndicator, Indicator<Num> subtractIndicator) {
        this.baseIndicator = baseIndicator;
        this.subtractIndicator = subtractIndicator;
    }

    @Override
    public Num getValue(int index) {
        return baseIndicator.getValue(index).minus(subtractIndicator.getValue(index));
    }

    @Override
    public int getUnstableBars() {
        return Math.max(baseIndicator.getUnstableBars(), subtractIndicator.getUnstableBars());
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
