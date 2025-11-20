package com.becker.freelance.indicators.ta.temporal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.timeseries.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

public interface TemporalBarSeries {

    boolean isEmpty();

    void addBar(TimeSeriesEntry currentPrice);

    public Duration getPairDuration();

    LocalDateTime getMinTime();

    int getSize();

    public void reset();

    public Pair getPair();

    public default LocalDateTime getLastTime(LocalDateTime time) {
        return time.minus(getPairDuration());
    }

    default boolean isTimeAligned(LocalDateTime time) {
        return TimeUtil.isAligned(time, getPairDuration());
    }

    TimeSeriesEntry getEntry(LocalDateTime index);
}
