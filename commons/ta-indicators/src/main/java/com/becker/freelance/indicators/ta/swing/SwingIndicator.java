package com.becker.freelance.indicators.ta.swing;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class SwingIndicator<T extends SwingPoint> extends CachableIndicator<Integer, T> implements Indicator<Optional<T>> {

    protected final int period;
    protected final Indicator<Num> estimationIndicator;
    private final PeriodPredicate beforeCandlePredicate;
    private final PeriodPredicate afterCandlePredicate;


    SwingIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize, PeriodPredicate beforeCandlePredicate, PeriodPredicate afterCandlePredicate) {
        super(cacheSize);
        this.period = period;
        this.estimationIndicator = estimationIndicator;
        this.beforeCandlePredicate = beforeCandlePredicate;
        this.afterCandlePredicate = afterCandlePredicate;
    }

    protected boolean isUnstable(int index) {
        return index + period > getBarSeries().getEndIndex() || index - period < 0;
    }

    protected Num[] getAfterCandle(boolean unstable, int index) {
        Num[] afterCandle = new Num[period];
        int idx = 0;
        int maxIdx = Math.min(unstable ? getBarSeries().getEndIndex() : index + period + 1, afterCandle.length - 1);
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
        if (cachePoint.isPresent() && !cachePoint.get().unstable()) return cachePoint;

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

    public Stream<T> recalculateAllStable(int fromIndex, int toIndex) {
        return IntStream.range(fromIndex, toIndex + 1)
                .mapToObj(this::getValue)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(SwingPoint::stable);
    }

    public Stream<T> recalculateAllStable(int toIndex) {
        return recalculateAllStable(0, toIndex);
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
