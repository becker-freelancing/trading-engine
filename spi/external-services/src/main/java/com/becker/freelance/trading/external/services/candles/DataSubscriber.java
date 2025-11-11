package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;

public interface DataSubscriber {

    void consume(TimeSeries timeSeries, LocalDateTime time);

    void onMissingData(LocalDateTime time);
}
