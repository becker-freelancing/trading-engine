package com.becker.freelance.indicators.ta.supportresistence;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.List;
import java.util.stream.IntStream;

public class SymetricRollingWindowIndicator implements Indicator<List<Num>> {

    private final Indicator<Num> source;
    private final int period;

    public SymetricRollingWindowIndicator(Indicator<Num> source, int period) {
        this.source = source;
        this.period = period;
    }

    public SymetricRollingWindowIndicator(BarSeries source, int period) {
        this(new ClosePriceIndicator(source), period);
    }

    @Override
    public List<Num> getValue(int index) {

        int startIdx = index - period;
        int endIndex = index + period + 1;

        if (startIdx < 0) {
            return List.of();
        }

        if (endIndex > getBarSeries().getBarCount()) {
            endIndex = getBarSeries().getEndIndex();
        }

        return IntStream.range(startIdx, endIndex)
                .mapToObj(source::getValue).toList();
    }

    @Override
    public int getUnstableBars() {
        return period;
    }

    @Override
    public BarSeries getBarSeries() {
        return source.getBarSeries();
    }
}
