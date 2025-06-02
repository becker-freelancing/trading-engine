package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Optional;

public class VolatilityIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Optional<Num>> varianceIndicator;

    public VolatilityIndicator(Indicator<Num> closePriceIndicator, int period) {
        LogReturnIndicator logReturnIndicator = new LogReturnIndicator(closePriceIndicator);
        RollingMeanIndicator rollingMeanIndicator = new RollingMeanIndicator(logReturnIndicator, period);
        this.varianceIndicator = new RollingVarianceIndicator(rollingMeanIndicator, period);
    }

    @Override
    public Optional<Num> getValue(int index) {
        return varianceIndicator.getValue(index).map(Num::sqrt);
    }

    @Override
    public int getUnstableBars() {
        return varianceIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return varianceIndicator.getBarSeries();
    }
}
