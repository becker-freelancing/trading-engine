package com.becker.freelance.data;

import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;

public interface DataSubscriber {

    public void consume(TimeSeries timeSeries, LocalDateTime time);

}
