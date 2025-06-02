package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class RollingMeanIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Num> baseIndicator;
    private final int meanPeriod;
    private final Num meanPeriodNum;
    private final Queue<Num> meanValues;
    private int lastIndex = -1;
    private Num currentSum = DecimalNum.valueOf(0);

    public RollingMeanIndicator(Indicator<Num> baseIndicator, int meanPeriod) {
        this.baseIndicator = baseIndicator;
        this.meanPeriod = meanPeriod;
        this.meanPeriodNum = DecimalNum.valueOf(meanPeriod);
        this.meanValues = new ArrayDeque<>(meanPeriod);
    }

    @Override
    public Optional<Num> getValue(int index) {
        if (lastIndex + 1 != index) {
            currentSum = DecimalNum.valueOf(0);
            meanValues.clear();
        }
        lastIndex = index;
        Num baseValue = baseIndicator.getValue(index);
        currentSum = currentSum.plus(baseValue);

        meanValues.add(baseValue);

        if (meanValues.size() > meanPeriod) {
            Num head = meanValues.poll();
            currentSum = currentSum.minus(head);
        } else if (meanValues.size() < meanPeriod) {
            return Optional.empty();
        }

        return Optional.ofNullable(currentSum.dividedBy(meanPeriodNum));
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
