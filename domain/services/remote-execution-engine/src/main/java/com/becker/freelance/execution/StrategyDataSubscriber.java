package com.becker.freelance.execution;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.trading.external.services.candles.DataSubscriber;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;

import java.time.LocalDateTime;
import java.util.Set;

public class StrategyDataSubscriber implements DataSubscriber {

    private final StrategyEngine strategyEngine;
    private final Set<TimeChangeListener> timeChangeListeners;

    public StrategyDataSubscriber(StrategyEngine strategyEngine, Set<TimeChangeListener> timeChangeListeners) {
        this.strategyEngine = strategyEngine;
        this.timeChangeListeners = timeChangeListeners;
    }


    @Override
    public void consume(TimeSeries timeSeries, LocalDateTime time) {
        timeChangeListeners.forEach(timeChangeListener -> timeChangeListener.onTimeChange(time));
        strategyEngine.update(timeSeries, time);
    }

    @Override
    public void onMissingData(LocalDateTime time) {
        strategyEngine.onMissingData(time);
    }
}
