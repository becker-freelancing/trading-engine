package com.becker.freelance.trading.external.services.backtest.earlystop;

import com.becker.freelance.trading.external.services.metric.Metric;

import java.util.Optional;

public class NoStopEarlyStopCallbackResult implements EarlyStopCallbackResult {

    @Override
    public boolean shouldStop() {
        return false;
    }

    @Override
    public Optional<Metric<?>> stopReason() {
        return Optional.empty();
    }
}
