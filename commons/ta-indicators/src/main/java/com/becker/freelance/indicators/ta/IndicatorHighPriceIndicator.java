package com.becker.freelance.indicators.ta;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.num.Num;

public class IndicatorHighPriceIndicator extends HighPriceIndicator {

    private final Indicator<Num> indicator;

    public IndicatorHighPriceIndicator(Indicator<Num> indicator) {
        super(indicator.getBarSeries());

        this.indicator = indicator;
    }

    @Override
    public Num getValue(int index) {
        return indicator.getValue(index);
    }

    @Override
    public int getUnstableBars() {
        return indicator.getUnstableBars();
    }
}
