package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;

import java.time.LocalDateTime;

public class NoTimeSeriesEntryFoundException extends RuntimeException{

    private final Pair pair;
    private final LocalDateTime time;

    public NoTimeSeriesEntryFoundException(Pair pair, LocalDateTime time) {
        super("No time found in Time Series " + pair + " before " + time);
        this.pair = pair;
        this.time = time;
    }
}
