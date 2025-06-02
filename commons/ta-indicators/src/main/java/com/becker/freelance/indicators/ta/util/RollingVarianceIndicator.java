package com.becker.freelance.indicators.ta.util;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class RollingVarianceIndicator implements Indicator<Optional<Num>> {

    private final Indicator<Optional<Num>> baseIndicator;
    private final int variancePeriod;
    private final Num variancePeriodNum;
    private final Queue<Num> varValues;
    private int lastIndex = -1;
    private Num currentSum = DecimalNum.valueOf(0);
    private Num currentSumSq = DecimalNum.valueOf(0);
    public RollingVarianceIndicator(Indicator<Optional<Num>> baseIndicator, int variancePeriod) {
        this.baseIndicator = baseIndicator;
        this.variancePeriod = variancePeriod;
        this.variancePeriodNum = DecimalNum.valueOf(variancePeriod);
        this.varValues = new ArrayDeque<>(variancePeriod);
    }

    public static RollingVarianceIndicator ofBaseIndicator(Indicator<Num> baseIndicator, int variancePeriod) {
        return new RollingVarianceIndicator(new OptionalIndicator(baseIndicator), variancePeriod);
    }

    @Override
    public Optional<Num> getValue(int index) {
        Optional<Num> optionalBaseValue = baseIndicator.getValue(index);
        if (lastIndex + 1 != index || optionalBaseValue.isEmpty()) {
            currentSum = DecimalNum.valueOf(0);
            currentSumSq = DecimalNum.valueOf(0);
            varValues.clear();
        }
        if (optionalBaseValue.isEmpty()) {
            return Optional.empty();
        }
        lastIndex = index;
        Num baseValue = optionalBaseValue.get();
        currentSum = currentSum.plus(baseValue);
        currentSumSq = currentSumSq.plus(baseValue.multipliedBy(baseValue));

        varValues.add(baseValue);

        if (varValues.size() > variancePeriod) {
            Num head = varValues.poll();
            currentSum = currentSum.minus(head);
            currentSumSq = currentSumSq.minus(head.multipliedBy(head));
        } else if (varValues.size() < variancePeriod) {
            return Optional.empty();
        }

        Num mean = currentSum.dividedBy(variancePeriodNum);
        Num meanSq = currentSumSq.dividedBy(variancePeriodNum);
        return Optional.ofNullable(meanSq.minus(mean.multipliedBy(mean)));
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars() + variancePeriod;
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }

    private static final record OptionalIndicator(Indicator<Num> indicator) implements Indicator<Optional<Num>> {
        @Override
        public Optional<Num> getValue(int index) {
            return Optional.of(indicator.getValue(index));
        }

        @Override
        public int getUnstableBars() {
            return indicator.getUnstableBars();
        }

        @Override
        public BarSeries getBarSeries() {
            return indicator.getBarSeries();
        }
    }
}
