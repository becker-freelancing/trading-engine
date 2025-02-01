package com.becker.freelance.engine;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.NoTimeSeriesEntryFoundException;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public class StrategyEngine {

    private final BaseStrategy strategy;
    private final TradeExecutor tradeExecutor;

    public StrategyEngine(BaseStrategy strategy, TradeExecutor tradeExecutor) {
        if (!strategy.isInitiatedForParameter()){
            throw new IllegalArgumentException("Strategy must be initiated for parameters but wasn't");
        }
        this.strategy = strategy;
        this.tradeExecutor = tradeExecutor;
    }

    public void executeForTime(TimeSeries timeSeries, LocalDateTime time){
        try {
            TimeSeriesEntry currentPrice = timeSeries.getEntryForTime(time);

            adaptPositions(currentPrice);
            closePositionsIfSlOrTpReached(currentPrice);

            shouldExit(currentPrice, timeSeries, time);
            shouldEnter(currentPrice, timeSeries, time);
        } catch (NoTimeSeriesEntryFoundException ignored){}
    }

    private void adaptPositions(TimeSeriesEntry currentPrice) {
        tradeExecutor.adaptPositions(currentPrice);
    }

    private void shouldEnter(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time) {
        Optional<EntrySignal> entrySignal = strategy.shouldEnter(timeSeries, time);
        entrySignal.ifPresent(signal -> tradeExecutor.entry(currentPrice, timeSeries, time, signal));
    }

    private void shouldExit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time) {
        Optional<ExitSignal> exitSignal = strategy.shouldExit(timeSeries, time);
        exitSignal.ifPresent(signal -> tradeExecutor.exit(currentPrice, timeSeries, time, signal));
    }

    private void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        tradeExecutor.closePositionsIfSlOrTpReached(currentPrice);
    }

    public TradeExecutor getTradeExecutor() {
        return tradeExecutor;
    }
}
