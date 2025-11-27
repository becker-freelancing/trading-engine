package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CompleteTimeSeries implements TimeSeries {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final Set<LocalDateTime> index;
    private final Pair pair;
    private final Map<LocalDateTime, TimeSeriesEntry> data;

    public CompleteTimeSeries(Pair pair, Map<LocalDateTime, TimeSeriesEntry> data) {
        this.pair = pair;
        this.data = new HashMap<>(data);
        this.index = new HashSet<>(data.keySet());
    }

    public CompleteTimeSeries(Pair pair, List<TimeSeriesEntry> initiationData) {
        this(pair, initiationData.stream().collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry)));
    }


    @Override
    public void addEntry(TimeSeriesEntry timeSeriesEntry) {
        Pair pair1 = timeSeriesEntry.pair();
        if (!pair.equals(pair1)) {
            return;
        }
        LocalDateTime time = timeSeriesEntry.time();
        index.add(time);
        data.put(time, timeSeriesEntry);
    }

    @Override
    public int getBarCount() {
        return index.size();
    }

    @Override
    public void clear() {
        index.clear();
        data.clear();
    }

    public TimeSeriesEntry getEntryForTime(LocalDateTime time) {
        if (!data.containsKey(time)) {
            if (getMinTime().isAfter(time)) {
                throw new NoTimeSeriesEntryFoundException(pair, time);
            }
            do {
                time = time.minus(pair.toDuration());
            } while (!data.containsKey(time));
        }
        return data.get(time);
    }

    public LocalDateTime getMinTime() {
        return data.keySet().stream().min(Comparator.naturalOrder()).orElseThrow(() -> new IllegalStateException("No data found"));
    }

    public LocalDateTime getMaxTime() {
        return data.keySet().stream().max(Comparator.naturalOrder()).orElseThrow(() -> new IllegalStateException("No data found"));
    }

    public Optional<List<TimeSeriesEntry>> getLastNCloseForTimeAsEntryIfExist(LocalDateTime endTime, int n) {
        try {

            List<TimeSeriesEntry> closes = new ArrayList<>();
            LocalDateTime start = endTime.minus(Duration.ofMinutes(pair.timeInMinutes() * n));

            while (start.isBefore(endTime) || start.isEqual(endTime)) {
                closes.add(getEntryForTime(start));
                start = start.plus(pair.toDuration());
            }

            return Optional.of(closes);

        } catch (NoTimeSeriesEntryFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return String.format("CompleteTimeSeries(For: %s, From: %s, To: %s, Entries: %d)",
                pair.technicalName(), getMinTime(), getMaxTime(), index.size());
    }

    public Pair getPair() {
        return pair;
    }

    public TimeSeriesEntry getLastEntryForTime(LocalDateTime time) {
        return getEntryForTime(time.minus(pair.toDuration()));
    }

    public boolean hasTime(LocalDateTime time) {
        return index.contains(time);
    }


    @Override
    public Iterator<TimeSeriesEntry> iterator() {
        return data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList()
                .iterator();
    }
}
