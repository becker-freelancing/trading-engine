package com.becker.freelance.indicators.ta.symetric;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.*;

public class SymetricSmaIndicator implements Indicator<Num> {

    private final SymetricRollingWindowIndicator symetricRollingWindowIndicator;
    private final Map<Integer, Num> cache;
    private final int period;

    public SymetricSmaIndicator(Indicator<Num> source, int period) {
        this.period = period;
        symetricRollingWindowIndicator = new SymetricRollingWindowIndicator(source, period);
        cache = new HashMap<>();
    }

    public SymetricSmaIndicator(BarSeries source, int period) {
        this(new ClosePriceIndicator(source), period);
    }

    @Override
    public Num getValue(int index) {
        List<Num> values = symetricRollingWindowIndicator.getValue(index);
        Num result = values.stream()
                .reduce(Num::plus)
                .map(sum -> sum.dividedBy(DecimalNum.valueOf(values.size())))
                .orElse(DecimalNum.valueOf(0));
        cache.put(index, result);
        return result;
    }

    public List<Num> getValues(int fromIdx, int toIdx) {
        List<Num> result = new ArrayList<>();

        Integer maxCacheIdx = cache.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
        if (toIdx - period <= maxCacheIdx) {
            for (int i = toIdx - period; i <= toIdx; i++) {
                getValue(i);
            }
        }

        Set<Integer> keySet = cache.keySet();
        for (int i = fromIdx; i < toIdx; i++) {
            if (!keySet.contains(i)) {
                result.add(getValue(i));
            } else {
                result.add(cache.get(i));
            }
        }

        return result;
    }

    @Override
    public int getUnstableBars() {
        return symetricRollingWindowIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return symetricRollingWindowIndicator.getBarSeries();
    }
}
