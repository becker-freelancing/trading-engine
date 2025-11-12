package com.becker.freelance.indicators.ta.temporal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeUtil;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TemporalBarSeriesImpl implements TemporalBarSeries {

    private final Pair pair;
    private final TemporalTa4JBarSeries barSeries;
    private final Map<LocalDateTime, Integer> indices;
    private final Map<Integer, LocalDateTime> times;
    private LocalDateTime minTime;

    public TemporalBarSeriesImpl(Pair pair) {
        this.pair = pair;
        this.barSeries = new TemporalTa4JBarSeries(pair.shortName());
        this.indices = new HashMap<>();
        this.times = new HashMap<>();
    }

    @Override
    public int mapTimeToIndex(LocalDateTime time) {
        if (!indices.containsKey(time)) {
            throw new IllegalStateException("No index found at time " + time + " for pair " + pair.technicalName());
        }
        return indices.get(time);
    }

    @Override
    public int mapTimeToLastAvailableIndex(LocalDateTime time) {
        return mapTimeToIndex(TimeUtil.lastAligned(time, getPairDuration()));
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
        if (!times.containsKey(index)) {
            throw new IllegalStateException("No time found at index " + index + " for pair " + pair.technicalName());
        }
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

    @Override
    public void reset() {
        barSeries.clear();
        times.clear();
        indices.clear();
    }
}
