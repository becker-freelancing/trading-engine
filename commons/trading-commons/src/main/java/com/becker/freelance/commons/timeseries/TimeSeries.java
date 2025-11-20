package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSeries {

    TimeSeriesEntry getEntryForTime(LocalDateTime time);

    LocalDateTime getMinTime();

    LocalDateTime getMaxTime();

    Optional<List<TimeSeriesEntry>> getLastNCloseForTimeAsEntryIfExist(LocalDateTime endTime, int n);

    @Override
    String toString();

    Pair getPair();

    TimeSeriesEntry getLastEntryForTime(LocalDateTime time);

    boolean hasTime(LocalDateTime time);

    default boolean isTimeEntryPossible(LocalDateTime time) {
        return TimeUtil.isAligned(time, getPair().toDuration());
    }

    void addEntry(TimeSeriesEntry timeSeriesEntry);

    int getBarCount();

    default boolean isEmpty() {
        return getBarCount() == 0;
    }

    void clear();
}
