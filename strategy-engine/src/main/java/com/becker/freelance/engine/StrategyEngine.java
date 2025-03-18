package com.becker.freelance.engine;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.NoTimeSeriesEntryFoundException;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

public class StrategyEngine {

    private final Logger logger = LoggerFactory.getLogger(StrategyEngine.class);

    private final Map<Pair, TimeSeries> timeSeries;
    private final Map<Pair, BaseStrategy> strategies;
    private final TradeExecutor tradeExecutor;

    public StrategyEngine(Map<Pair, TimeSeries> timeSeries, Supplier<BaseStrategy> strategySupplier, TradeExecutor tradeExecutor) {
        if (!strategySupplier.get().isInitiatedForParameter()) {
            throw new IllegalArgumentException("Strategy must be initiated for parameters but wasn't");
        }
        this.tradeExecutor = tradeExecutor;
        this.timeSeries = timeSeries;
        this.strategies = new HashMap<>();
        timeSeries.keySet().forEach(pair -> {
            strategies.put(pair, strategySupplier.get());
        });
    }

    public void execute() {
        Iterator<LocalDateTime> times = timeSeries.values().stream().findFirst().map(TimeSeries::iterator).orElseThrow(IllegalStateException::new);
        Set<Pair> pairs = timeSeries.keySet();

        while (times.hasNext()) {
            LocalDateTime time = times.next();

            for (Pair pair : pairs) {
                TimeSeries currentSeries = timeSeries.get(pair);
                BaseStrategy strategy = strategies.get(pair);
                executeForTime(currentSeries, time, strategy);
            }
        }
    }

    public void executeForTime(TimeSeries timeSeries, LocalDateTime time, BaseStrategy strategy) {
        try {
            TimeSeriesEntry currentPrice = timeSeries.getEntryForTime(time);

            adaptPositions(currentPrice);
            closePositionsIfSlOrTpReached(currentPrice);

            shouldExit(currentPrice, timeSeries, time, strategy);
            shouldEnter(currentPrice, timeSeries, time, strategy);
        } catch (NoTimeSeriesEntryFoundException e) {
            logger.error("Error while executing Strategy {}", strategy.getClass().getName(), e);
        }
    }

    private void adaptPositions(TimeSeriesEntry currentPrice) {
        tradeExecutor.adaptPositions(currentPrice);
    }

    private void shouldEnter(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, BaseStrategy strategy) {
        Optional<EntrySignal> entrySignal = strategy.shouldEnter(timeSeries, time);
        entrySignal.ifPresent(signal -> tradeExecutor.entry(currentPrice, timeSeries, time, signal));
    }

    private void shouldExit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, BaseStrategy strategy) {
        Optional<ExitSignal> exitSignal = strategy.shouldExit(timeSeries, time);
        exitSignal.ifPresent(signal -> tradeExecutor.exit(currentPrice, timeSeries, time, signal));
    }

    private void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        tradeExecutor.closePositionsIfSlOrTpReached(currentPrice);
    }

}
