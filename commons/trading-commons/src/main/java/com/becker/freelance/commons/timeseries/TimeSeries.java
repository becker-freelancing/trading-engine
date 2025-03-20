package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;
import org.ta4j.core.Bar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSeries {

    public TimeSeriesEntry getEntryForTime(LocalDateTime time);


    public Bar getEntryForTimeAsBar(LocalDateTime time);

    public LocalDateTime getMinTime();

    public LocalDateTime getMaxTime();

    public Optional<List<TimeSeriesEntry>> getLastNCloseForTimeAsEntryIfExist(LocalDateTime endTime, int n);

    @Override
    public String toString();

    public Pair getPair();

    public TimeSeriesEntry getLastEntryForTime(LocalDateTime time);

    public boolean hasTime(LocalDateTime time);
}
