package com.becker.freelance.backtest;

import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataSubscriber;
import com.becker.freelance.engine.StrategyEngine;

import java.time.LocalDateTime;

public class StrategyDataSubscriber implements DataSubscriber {

    private final StrategyEngine strategyEngine;

    public StrategyDataSubscriber(StrategyEngine strategyEngine) {
        this.strategyEngine = strategyEngine;
    }


    @Override
    public void consume(TimeSeries timeSeries, LocalDateTime time) {
        strategyEngine.update(timeSeries, time);
    }
}
