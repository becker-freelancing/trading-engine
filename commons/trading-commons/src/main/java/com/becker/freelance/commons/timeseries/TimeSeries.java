package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class TimeSeries implements Iterable<LocalDateTime>{
    Set<LocalDateTime> index;
    Pair pair;
    Map<LocalDateTime, TimeSeriesEntry> data;
    Map<LocalDateTime, Bar> barData;

    public TimeSeries(Pair pair, Map<LocalDateTime, TimeSeriesEntry> data) {
        this.pair = pair;
        this.data = data;
        this.index = data.keySet();
        ZoneId utc = ZoneId.of("UTC");
        this.barData = data.entrySet().stream().parallel().map(entry -> {
            TimeSeriesEntry value = entry.getValue();
            return new BaseBar(pair.toDuration(), entry.getKey().atZone(utc),
                    value.openMid(), value.highMid(), value.lowMid(), value.closeMid(), value.getVolume());
        }).collect(Collectors.toMap(
                bar -> bar.getEndTime().toLocalDateTime(),
                bar -> bar
        ));
    }


    public TimeSeriesEntry getEntryForTime(LocalDateTime time) {
        if (!data.containsKey(time)) {
            if (getMinTime().isAfter(time)){
                throw new NoTimeSeriesEntryFoundException(pair, time);
            }
            do {
                time = time.minus(pair.toDuration());
            } while (!data.containsKey(time));
        }
        return data.get(time);
    }


    public Bar getEntryForTimeAsBar(LocalDateTime time) {
        if (!barData.containsKey(time)) {
            if (getMinTime().isAfter(time)){
                throw new NoTimeSeriesEntryFoundException(pair, time);
            }
            do {
                time = time.minus(pair.toDuration());
            } while (!barData.containsKey(time));
        }
        return barData.get(time);
    }

    public LocalDateTime getMinTime() {
        return data.keySet().stream().min(Comparator.naturalOrder()).orElseThrow(() -> new IllegalStateException("No data found"));
    }

    public LocalDateTime getMaxTime() {
        return data.keySet().stream().max(Comparator.naturalOrder()).orElseThrow(() -> new IllegalStateException("No data found"));
    }

    public TimeSeries slice(LocalDateTime fromTime, LocalDateTime toTime) {
        Map<LocalDateTime, TimeSeriesEntry> slicedData = new TreeMap<>();

        for (LocalDateTime time : index) {
            if (!time.isBefore(fromTime) && !time.isAfter(toTime)) {
                slicedData.put(time, data.get(time));
            }
        }

        return new TimeSeries(pair, slicedData);
    }

    public List<Double> getLastNCloseForTime(LocalDateTime endTime, int n) {
        List<Double> closes = new ArrayList<>();
        LocalDateTime start = endTime.minus(Duration.ofMinutes(pair.timeInMinutes() * n));

        while (start.isBefore(endTime) || start.isEqual(endTime)) {
            closes.add(getEntryForTime(start).closeMid());
            start = start.plus(pair.toDuration());
        }

        return closes;
    }

    public List<TimeSeriesEntry> getLastNCloseForTimeAsEntry(LocalDateTime endTime, int n) {
        List<TimeSeriesEntry> closes = new ArrayList<>();
        LocalDateTime start = endTime.minus(Duration.ofMinutes(pair.timeInMinutes() * n));

        while (start.isBefore(endTime) || start.isEqual(endTime)) {
            closes.add(getEntryForTime(start));
            start = start.plus(pair.toDuration());
        }

        return closes;
    }

    public BarSeries getLastNCloseForTimeAsBarSeries(LocalDateTime endTime, int n) {
        List<Bar> closes = new ArrayList<>();
        LocalDateTime start = endTime.minus(Duration.ofMinutes(pair.timeInMinutes() * n));

        while (start.isBefore(endTime) || start.isEqual(endTime)) {
            closes.add(getEntryForTimeAsBar(start));
            start = start.plus(pair.toDuration());
        }

        return new BaseBarSeriesBuilder().withBars(closes).build();
    }

    @Override
    public String toString() {
        return String.format("TimeSeries(For: %s, From: %s, To: %s, Entries: %d)",
                pair.technicalName(), getMinTime(), getMaxTime(), index.size());
    }

    public Pair getPair() {
        return pair;
    }

    public boolean containsBar(LocalDateTime time) {
        return barData.containsKey(time);
    }

    public TimeSeriesEntry getLastEntryForTime(LocalDateTime time) {
        return getEntryForTime(time.minus(pair.toDuration()));
    }

    @Override
    public Iterator<LocalDateTime> iterator() {
        return index.stream().sorted(Comparator.naturalOrder()).iterator();
    }

    public Collection<LocalDateTime> iterator(LocalDateTime minTime, LocalDateTime maxTime) {
        return index.stream().filter(t -> minTime.isBefore(t) && maxTime.isAfter(t)).sorted(Comparator.naturalOrder()).toList();
    }
}
