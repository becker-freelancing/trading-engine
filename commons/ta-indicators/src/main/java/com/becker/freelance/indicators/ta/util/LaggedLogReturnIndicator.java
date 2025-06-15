package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class LaggedLogReturnIndicator implements Indicator<Num> {

    private final Indicator<Num> closePrice;
    private final int lag;

    public LaggedLogReturnIndicator(Indicator<Num> closePrice, int lag) {
        this.closePrice = closePrice;
        this.lag = lag;
    }

    @Override
    public Num getValue(int index) {
        return closePrice.getValue(index).dividedBy(closePrice.getValue(index - lag)).log();
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + lag + 1;
    }

    @Override
    public BarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}
