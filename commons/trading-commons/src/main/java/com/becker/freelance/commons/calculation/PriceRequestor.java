package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface PriceRequestor {

    public TimeSeriesEntry getPriceForTime(Pair pair, LocalDateTime time);

    public default List<TimeSeriesEntry> getPriceInRange(Pair pair, LocalDateTime from, LocalDateTime to) {
        List<TimeSeriesEntry> data = new ArrayList<>();
        while (from.isBefore(to)) {
            data.add(getPriceForTime(pair, from));
            from = from.plusMinutes(pair.timeInMinutes());
        }
        data.add(getPriceForTime(pair, from));
        return data;
    }
}
