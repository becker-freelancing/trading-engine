package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.Optional;

public class SwingLowIndicator implements Indicator<Optional<SwingLowPoint>> {

    private final int period;
    private final Indicator<Num> estimationIndicator;
    private final Map<Integer, SwingLowPoint> cache;

    public SwingLowIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize) {
        this.period = period;
        this.estimationIndicator = estimationIndicator;
        this.cache = new FixedSizeMap<>(cacheSize);
    }

    public SwingLowIndicator(int period, Indicator<Num> estimationIndicator) {
        this(period, estimationIndicator, 1000);
    }


    @Override
    public Optional<SwingLowPoint> getValue(int index) {
        if (cache.containsKey(index)) {
            SwingLowPoint swingHighPoint = cache.get(index);
            if (!swingHighPoint.unstable()) {
                return Optional.of(swingHighPoint);
            }
        }

        boolean unstable = index + period > getBarSeries().getEndIndex();
        Num candleValue = estimationIndicator.getValue(index);

        Num[] beforeCandle = getBeforeCandle(index);
        for (Num num : beforeCandle) {
            if (num.isLessThan(candleValue)) {
                return Optional.empty();
            }
        }

        Num[] afterCandle = getAfterCandle(unstable, index);
        boolean afterCandleLess = true;
        for (Num num : afterCandle) {
            if (num != null && num.isLessThan(candleValue)) {
                afterCandleLess = false;
            }
        }

        if (afterCandleLess) {
            SwingLowPoint swingLowPoint = new SwingLowPoint(unstable, index, candleValue);
            cache.put(index, swingLowPoint);
            return Optional.of(
                    swingLowPoint
            );
        }

        return Optional.empty();
    }

    private Num[] getAfterCandle(boolean unstable, int index) {
        Num[] afterCandle = new Num[period];
        int idx = 0;
        int maxIdx = unstable ? getBarSeries().getEndIndex() : index + period + 1;
        for (int i = index + 1; i < maxIdx; i++) {
            afterCandle[idx] = estimationIndicator.getValue(i);
            idx++;
        }
        return afterCandle;
    }

    private Num[] getBeforeCandle(int index) {
        Num[] beforeCandle = new Num[period];
        int idx = 0;
        for (int i = index - period; i < index; i++) {
            beforeCandle[idx] = estimationIndicator.getValue(i);
            idx++;
        }
        return beforeCandle;
    }

    @Override
    public int getUnstableBars() {
        return estimationIndicator.getUnstableBars() + period;
    }

    @Override
    public BarSeries getBarSeries() {
        return estimationIndicator.getBarSeries();
    }
}
