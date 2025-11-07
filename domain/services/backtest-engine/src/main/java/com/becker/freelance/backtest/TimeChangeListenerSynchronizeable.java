package com.becker.freelance.backtest;

import com.becker.freelance.trading.external.services.candles.Synchronizeable;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;

import java.time.LocalDateTime;

public record TimeChangeListenerSynchronizeable(TimeChangeListener timeChangeListener) implements Synchronizeable {

    @Override
    public void synchronize(LocalDateTime time) {
        timeChangeListener.onTimeChange(time);
    }
}
