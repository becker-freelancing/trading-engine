package com.becker.freelance.indicators.ta.temporal.series;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.QueueTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.Duration;
import java.time.LocalDateTime;

public class TemporalBarSeriesImpl implements TemporalBarSeries {

    private final Pair pair;
    private final TimeSeries barSeries;
    private LocalDateTime minTime;

    public TemporalBarSeriesImpl(Pair pair) {
        this.pair = pair;
        this.barSeries = new QueueTimeSeries(pair, 1000);
    }

    @Override
    public boolean isEmpty() {
        return barSeries.isEmpty();
    }

    @Override
    public void addBar(TimeSeriesEntry currentPrice) {
        barSeries.addEntry(currentPrice);
        LocalDateTime time = currentPrice.time();
        if (minTime == null || time.isBefore(minTime)) {
            minTime = time;
        }
    }

    @Override
    public Duration getPairDuration() {
        return pair.toDuration();
    }

    @Override
    public LocalDateTime getMinTime() {
        return minTime;
    }

    @Override
    public int getSize() {
        return barSeries.getBarCount();
    }

    @Override
    public void reset() {
        barSeries.clear();
    }

    @Override
    public Pair getPair() {
        return pair;
    }

    @Override
    public TimeSeriesEntry getEntry(LocalDateTime index) {
        return barSeries.getEntryForTime(index);
    }
}
