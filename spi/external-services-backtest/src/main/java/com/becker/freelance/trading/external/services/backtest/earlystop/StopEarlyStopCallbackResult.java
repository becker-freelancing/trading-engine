package com.becker.freelance.trading.external.services.backtest.earlystop;

import com.becker.freelance.trading.external.services.metric.Metric;

import java.util.Optional;

public class StopEarlyStopCallbackResult implements EarlyStopCallbackResult {
    private final Metric<?> stopReason;

    public StopEarlyStopCallbackResult(Metric<?> stopReason) {
        this.stopReason = stopReason;
    }

    @Override
    public boolean shouldStop() {
        return true;
    }

    @Override
    public Optional<Metric<?>> stopReason() {
        return Optional.of(stopReason);
    }
}
