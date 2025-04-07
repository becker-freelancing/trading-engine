package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class SwingLowIndicator extends SwingIndicator<SwingLowPoint> {

    public SwingLowIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize) {
        super(period, estimationIndicator, cacheSize,
                ((num, candleValue) -> num != null && num.isLessThan(candleValue)),
                ((num, candleValue) -> num != null && num.isLessThan(candleValue)));
    }

    public SwingLowIndicator(int period, Indicator<Num> estimationIndicator) {
        this(period, estimationIndicator, 1000);
    }

    @Override
    protected SwingLowPoint createResult(boolean unstable, int index, Num candleValue) {
        return new SwingLowPoint(unstable, index, candleValue);
    }
}
