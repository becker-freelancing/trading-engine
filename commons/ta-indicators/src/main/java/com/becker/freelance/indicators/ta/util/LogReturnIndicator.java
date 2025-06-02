package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class LogReturnIndicator implements Indicator<Num> {

    private final Indicator<Num> closePrice;

    public LogReturnIndicator(Indicator<Num> closePrice) {
        this.closePrice = closePrice;
    }

    @Override
    public Num getValue(int index) {
        return closePrice.getValue(index).dividedBy(closePrice.getValue(index - 1));
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + 1;
    }

    @Override
    public BarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}

