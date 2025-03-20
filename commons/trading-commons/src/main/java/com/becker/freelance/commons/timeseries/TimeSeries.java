package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;
import org.ta4j.core.Bar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSeries {

    TimeSeriesEntry getEntryForTime(LocalDateTime time);


    Bar getEntryForTimeAsBar(LocalDateTime time);

    LocalDateTime getMinTime();

    LocalDateTime getMaxTime();

    Optional<List<TimeSeriesEntry>> getLastNCloseForTimeAsEntryIfExist(LocalDateTime endTime, int n);

    @Override
    String toString();

    Pair getPair();

    TimeSeriesEntry getLastEntryForTime(LocalDateTime time);

    boolean hasTime(LocalDateTime time);
}
