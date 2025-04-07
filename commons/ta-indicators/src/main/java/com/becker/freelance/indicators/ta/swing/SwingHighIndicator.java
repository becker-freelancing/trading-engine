package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class SwingHighIndicator extends SwingIndicator<SwingHighPoint> {



    public SwingHighIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize) {
        super(period, estimationIndicator, cacheSize,
                (num, candleValue) -> num != null && num.isGreaterThan(candleValue),
                ((num, candleValue) -> num != null && num.isGreaterThan(candleValue)));
    }

    public SwingHighIndicator(int period, Indicator<Num> estimationIndicator) {
        this(period, estimationIndicator, 1000);
    }


    @Override
    protected SwingHighPoint createResult(boolean unstable, int index, Num candleValue) {
        return new SwingHighPoint(unstable, index, candleValue);
    }

}
