package com.becker.freelance.engine;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.NoTimeSeriesEntryFoundException;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.management.api.ManagementLoader;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.CompositeStrategy;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

public class StrategyEngine {

    private final Logger logger = LoggerFactory.getLogger(StrategyEngine.class);

    private final BaseStrategy strategy;
    private final TradeExecutor tradeExecutor;
    private final EntrySignalAdaptor entrySignalAdaptor;
    private final EntrySignalValidator entrySignalValidator;
    private ManagementEnvironmentProvider environmentProvider;

    public StrategyEngine(Supplier<BaseStrategy> strategySupplier, TradeExecutor tradeExecutor) {
        if (!strategySupplier.get().isInitiatedForParameter()) {
            throw new IllegalArgumentException("Strategy must be initiated for parameters but wasn't");
        }
        this.tradeExecutor = tradeExecutor;
        this.strategy = strategySupplier.get().withOpenPositionRequestor(tradeExecutor);
        ManagementLoader managementLoader = new ManagementLoader();
        this.entrySignalAdaptor = managementLoader.findEntrySignalAdaptor();
        this.entrySignalValidator = managementLoader.findEntrySignalValidator(CompositeStrategy.ALL_MATCH);
//        this.environmentProvider = managementLoader.findEnvironmentProvider();
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
        entrySignal
                .map(signal -> entrySignalAdaptor.adapt(environmentProvider, signal))
                .filter(signal -> entrySignalValidator.isValidToExecute(environmentProvider, signal))
                .ifPresent(signal -> tradeExecutor.entry(currentPrice, timeSeries, time, signal));
    }

    private void shouldExit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, BaseStrategy strategy) {
        Optional<ExitSignal> exitSignal = strategy.shouldExit(timeSeries, time);
        exitSignal.ifPresent(signal -> tradeExecutor.exit(currentPrice, timeSeries, time, signal));
    }

    private void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        tradeExecutor.closePositionsIfSlOrTpReached(currentPrice);
    }

    public void update(TimeSeries timeSeries, LocalDateTime time) {
        executeForTime(timeSeries, time, strategy);
    }
}
