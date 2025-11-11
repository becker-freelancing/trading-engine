package com.becker.freelance.indicators.ta.temporal;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import java.time.Duration;
import java.time.LocalDateTime;

public interface TemporalBarSeries {

    public int mapTimeToIndex(LocalDateTime time);

    public int mapTimeToLastAvailableIndex(LocalDateTime time);

    BarSeries getBarSeries();

    boolean isEmpty();

    void addBar(Bar currentPrice);

    LocalDateTime mapIndexToTime(int index);

    public Duration getPairDuration();

    LocalDateTime getMinTime();

    int getSize();

    Bar getBar(LocalDateTime index);

    public void reset();
}
