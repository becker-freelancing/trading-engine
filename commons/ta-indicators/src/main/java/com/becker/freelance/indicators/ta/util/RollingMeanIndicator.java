package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Optional;
import java.util.stream.IntStream;

public class RollingMeanIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Num> baseIndicator;
    private final int meanPeriod;
    private final Num meanPeriodNum;

    public RollingMeanIndicator(Indicator<Num> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = DecimalNum.valueOf(meanPeriod);
    }

    @Override
    public Optional<Num> getValue(int index) {
        return IntStream.rangeClosed(index - meanPeriod + 1, index)
                .mapToObj(baseIndicator::getValue)
                .reduce(Num::plus)
                .map(sum -> sum.dividedBy(meanPeriodNum));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars() + meanPeriod;
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
