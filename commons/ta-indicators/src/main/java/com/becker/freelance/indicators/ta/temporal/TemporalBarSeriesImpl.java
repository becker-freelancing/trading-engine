package com.becker.freelance.indicators.ta.temporal;

import com.becker.freelance.commons.pair.Pair;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TemporalBarSeriesImpl implements TemporalBarSeries {

    private final Pair pair;
    private final BarSeries barSeries;
    private final Map<LocalDateTime, Integer> indices;
    private final Map<Integer, LocalDateTime> times;
    private LocalDateTime minTime;

    public TemporalBarSeriesImpl(Pair pair) {
        this.pair = pair;
        this.barSeries = new BaseBarSeries();
        this.indices = new HashMap<>();
        this.times = new HashMap<>();
    }

    @Override
    public int mapTimeToIndex(LocalDateTime time) {
        return indices.get(time);
    }

    @Override
    public BarSeries getBarSeries() {
        return barSeries;
    }

    @Override
    public boolean isEmpty() {
        return barSeries.isEmpty();
    }

    @Override
    public void addBar(Bar currentPrice) {
        barSeries.addBar(currentPrice);
        LocalDateTime time = currentPrice.getEndTime().toLocalDateTime();
        times.put(barSeries.getEndIndex(), time);
        indices.put(time, barSeries.getEndIndex());
        if (minTime == null || time.isBefore(minTime)) {
            minTime = time;
        }
    }

    @Override
    public LocalDateTime mapIndexToTime(int index) {
        return times.get(index);
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
        return barSeries.getEndIndex();
    }

    @Override
    public Bar getBar(LocalDateTime time) {
        int i = mapTimeToIndex(time);
        return barSeries.getBar(i);
    }
}
