package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.numeric.NumericIndicator;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;

public class TemporalMACDIndicator implements TemporalIndicator<Num> {


    private final MACDIndicator macdIndicator;
    private final TemporalBarSeries barSeries;

    public TemporalMACDIndicator(TemporalIndicator<Num> indicator, int shortPeriod, int longPeriod) {
        this.barSeries = indicator.getBarSeries();
        this.macdIndicator = new MACDIndicator(new TemporalIndicatorToIndicator<>(indicator), shortPeriod, longPeriod);
    }

    @Override
    public Num getValue(LocalDateTime time) {
        int i = barSeries.mapTimeToIndex(time);
        return macdIndicator.getValue(i);
    }

    @Override
    public int getUnstableBars() {
        return macdIndicator.getUnstableBars();
    }

    @Override
    public TemporalBarSeries getBarSeries() {
        return barSeries;
    }

    public TemporalNumericIndicator getHistogram(int macdSignalLinePeriod) {
        NumericIndicator histogram = macdIndicator.getHistogram(macdSignalLinePeriod);
        return new TemporalNumericIndicator(barSeries, histogram);
    }

    public TemporalEMAIndicator getShortTermEma() {
        EMAIndicator shortTermEma = macdIndicator.getShortTermEma();
        return new TemporalEMAIndicator(barSeries, shortTermEma);
    }
}
