package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class QueueTimeSeries implements TimeSeries {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final Set<LocalDateTime> index;
    private final Pair pair;
    private final Map<LocalDateTime, TimeSeriesEntry> data;
    private final int maximumSize;
    private final Queue<LocalDateTime> timeQueue;
    private int barCount;

    public QueueTimeSeries(Pair pair, int maximumSize) {
        this.pair = pair;
        this.data = new HashMap<>();
        this.index = new HashSet<>();
        this.maximumSize = maximumSize;
        this.timeQueue = new LinkedList<>();
        this.barCount = 0;
    }


    @Override
    public void addEntry(TimeSeriesEntry timeSeriesEntry) {
        Pair pair1 = timeSeriesEntry.pair();
        if (!pair.equals(pair1)) {
            return;
        }

        if (timeQueue.size() >= maximumSize) {
            LocalDateTime oldestTime = timeQueue.poll();
            index.remove(oldestTime);
            data.remove(oldestTime);
        }

        LocalDateTime time = timeSeriesEntry.time();
        index.add(time);
        data.put(time, timeSeriesEntry);
        timeQueue.add(time);
        barCount++;
    }

    @Override
    public int getBarCount() {
        return barCount;
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
        if (data.isEmpty()) {
            return String.format("QueueTimeSeries(For: %s, Empty)",
                    pair.technicalName());
        }
        return String.format("QueueTimeSeries(For: %s, From: %s, To: %s, Entries: %d)",
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
    public void clear() {
        index.clear();
        data.clear();
        timeQueue.clear();
        barCount = 0;
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
