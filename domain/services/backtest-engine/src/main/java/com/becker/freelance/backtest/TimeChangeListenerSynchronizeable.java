package com.becker.freelance.backtest;

import com.becker.freelance.data.Synchronizeable;
import com.becker.freelance.management.api.environment.TimeChangeListener;

import java.time.LocalDateTime;

public record TimeChangeListenerSynchronizeable(TimeChangeListener timeChangeListener) implements Synchronizeable {

    @Override
    public void synchronize(LocalDateTime time) {
        timeChangeListener.onTimeChange(time);
    }
}
