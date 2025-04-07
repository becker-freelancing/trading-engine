package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.Optional;

abstract class SwingIndicator<T extends SwingPoint> implements Indicator<Optional<T>> {

    protected final int period;
    protected final Indicator<Num> estimationIndicator;
    private final Map<Integer, T> cache;
    private final PeriodPredicate beforeCandlePredicate;
    private final PeriodPredicate afterCandlePredicate;


    SwingIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize, PeriodPredicate beforeCandlePredicate, PeriodPredicate afterCandlePredicate) {
        this.period = period;
        this.estimationIndicator = estimationIndicator;
        this.cache = new FixedSizeMap<>(cacheSize);
        this.beforeCandlePredicate = beforeCandlePredicate;
        this.afterCandlePredicate = afterCandlePredicate;
    }


    protected Optional<T> findInCache(int index) {
        if (cache.containsKey(index)) {
            T swingPoint = cache.get(index);
            if (!swingPoint.unstable()) {
                return Optional.of(swingPoint);
            }
        }
        return Optional.empty();
    }

    protected void putInCache(int index, T swingHighPoint) {
        cache.put(index, swingHighPoint);
    }

    protected boolean isUnstable(int index) {
        return index + period > getBarSeries().getEndIndex() || index - period < 0;
    }

    protected Num[] getAfterCandle(boolean unstable, int index) {
        Num[] afterCandle = new Num[period];
        int idx = 0;
        int maxIdx = unstable ? getBarSeries().getEndIndex() : index + period + 1;
        for (int i = index + 1; i < maxIdx; i++) {
            afterCandle[idx] = estimationIndicator.getValue(i);
            idx++;
        }
        return afterCandle;
    }

    protected Num[] getBeforeCandle(int index) {
        Num[] beforeCandle = new Num[period];
        int idx = 0;
        for (int i = Math.max(index - period, 0); i < index; i++) {
            beforeCandle[idx] = estimationIndicator.getValue(i);
            idx++;
        }
        return beforeCandle;
    }

    @Override
    public Optional<T> getValue(int index) {
        Optional<T> cachePoint = findInCache(index);
        if (cachePoint.isPresent()) return cachePoint;

        boolean unstable = isUnstable(index);
        Num candleValue = estimationIndicator.getValue(index);

        Num[] beforeCandle = getBeforeCandle(index);
        for (Num num : beforeCandle) {
            if (beforeCandlePredicate.test(num, candleValue)) {
                return Optional.empty();
            }
        }

        Num[] afterCandle = getAfterCandle(unstable, index);
        boolean afterCandleLess = true;
        for (Num num : afterCandle) {
            if (afterCandlePredicate.test(num, candleValue)) {
                afterCandleLess = false;
                break;
            }
        }

        if (afterCandleLess) {
            T swingHighPoint = createResult(unstable, index, candleValue);
            putInCache(index, swingHighPoint);
            return Optional.of(
                    swingHighPoint
            );
        }

        return Optional.empty();
    }


    @Override
    public int getUnstableBars() {
        return estimationIndicator.getUnstableBars() + period;
    }

    @Override
    public BarSeries getBarSeries() {
        return estimationIndicator.getBarSeries();
    }

    protected abstract T createResult(boolean unstable, int index, Num candleValue);

    protected static interface PeriodPredicate {
        public boolean test(Num num, Num candleValue);
    }
}
