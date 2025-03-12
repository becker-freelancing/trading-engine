package com.becker.freelance.indicators.ta;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;

public class IndicatorLowPriceIndicator extends LowPriceIndicator {

    private final Indicator<Num> indicator;

    public IndicatorLowPriceIndicator(Indicator<Num> indicator) {
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
