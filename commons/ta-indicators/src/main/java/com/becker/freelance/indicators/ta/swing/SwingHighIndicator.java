package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.Optional;

public class SwingHighIndicator implements Indicator<Optional<SwingHighPoint>> {

    private final int period;
    private final Indicator<Num> estimationIndicator;
    private final Map<Integer, SwingHighPoint> cache;

    public SwingHighIndicator(int period, Indicator<Num> estimationIndicator, int cacheSize) {
        this.period = period;
        this.estimationIndicator = estimationIndicator;
        this.cache = new FixedSizeMap<>(cacheSize);
    }

    public SwingHighIndicator(int period, Indicator<Num> estimationIndicator) {
        this(period, estimationIndicator, 1000);
    }


    @Override
    public Optional<SwingHighPoint> getValue(int index) {
        if (cache.containsKey(index)) {
            SwingHighPoint swingHighPoint = cache.get(index);
            if (!swingHighPoint.unstable()) {
                return Optional.of(swingHighPoint);
            }
        }

        boolean unstable = index + period > getBarSeries().getEndIndex();
        Num candleValue = estimationIndicator.getValue(index);

        Num[] beforeCandle = getBeforeCandle(index);
        for (Num num : beforeCandle) {
            if (num.isGreaterThan(candleValue)) {
                return Optional.empty();
            }
        }

        Num[] afterCandle = getAfterCandle(unstable, index);
        boolean afterCandleLess = true;
        for (Num num : afterCandle) {
            if (num != null && num.isGreaterThan(candleValue)) {
                afterCandleLess = false;
            }
        }

        if (afterCandleLess) {
            SwingHighPoint swingHighPoint = new SwingHighPoint(unstable, index, candleValue);
            cache.put(index, swingHighPoint);
            return Optional.of(
                    swingHighPoint
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
