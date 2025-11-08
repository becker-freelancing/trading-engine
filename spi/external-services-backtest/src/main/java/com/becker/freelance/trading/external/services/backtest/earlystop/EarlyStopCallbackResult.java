package com.becker.freelance.trading.external.services.backtest.earlystop;

import com.becker.freelance.trading.external.services.metric.Metric;

import java.util.Optional;

public interface EarlyStopCallbackResult {

    public boolean shouldStop();

    public Optional<Metric<?>> stopReason();
}
