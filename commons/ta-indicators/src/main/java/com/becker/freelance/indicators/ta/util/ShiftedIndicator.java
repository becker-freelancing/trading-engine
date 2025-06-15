package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class ShiftedIndicator implements Indicator<Num> {

    private final Indicator<Num> closePrice;
    private final int shift;

    public ShiftedIndicator(Indicator<Num> closePrice, int shift) {
        this.closePrice = closePrice;
        this.shift = shift;
    }

    @Override
    public Num getValue(int index) {
        return closePrice.getValue(index - shift);
    }

    @Override
    public int getUnstableBars() {
        return closePrice.getUnstableBars() + shift;
    }

    @Override
    public BarSeries getBarSeries() {
        return closePrice.getBarSeries();
    }
}
