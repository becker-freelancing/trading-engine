package com.becker.freelance.data;

import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;

public interface DataSubscriber {

    void consume(TimeSeries timeSeries, LocalDateTime time);

}
