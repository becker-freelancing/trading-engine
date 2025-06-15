package com.becker.freelance.indicators.ta.cache;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CachedIndicator<V> extends CachableIndicator<Integer, V> implements Indicator<V> {

    private final Indicator<V> baseIndicator;

    public CachedIndicator(int cacheSize, Indicator<V> baseIndicator) {
        super(cacheSize);
        this.baseIndicator = baseIndicator;
    }


    @Override
    public V getValue(int index) {
        Optional<V> inCache = findInCache(index);
        if (inCache.isPresent()) {
            return inCache.get();
        }
        V value = baseIndicator.getValue(index);
        putInCache(index, value);
        return value;
    }


    public Stream<V> getLastNValues(int n) {
        int start = getBarSeries().getEndIndex() - n + 1;
        if (start < 0) {
            return Stream.of();
        }
        return IntStream.rangeClosed(start, getBarSeries().getEndIndex())
                .mapToObj(this::getValue);
    }

    @Override
    public int getUnstableBars() {
        return baseIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
