package com.becker.freelance.engine;

import com.becker.freelance.broker.BrokerRequestor;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
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
import com.becker.freelance.strategies.TradingStrategy;
import com.becker.freelance.strategies.executionparameter.DefaultEntryParameter;
import com.becker.freelance.strategies.executionparameter.DefaultExitParameter;
import com.becker.freelance.strategies.executionparameter.EntryParameter;
import com.becker.freelance.strategies.executionparameter.ExitParameter;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public class StrategyEngine {

    private final Logger logger = LoggerFactory.getLogger(StrategyEngine.class);

    private final TradingStrategy strategy;
    private final TradeExecutor tradeExecutor;
    private final EntrySignalAdaptor entrySignalAdaptor;
    private final EntrySignalValidator entrySignalValidator;
    private final ManagementEnvironmentProvider environmentProvider;

    public StrategyEngine(Pair pair, StrategySupplier strategySupplier, TradeExecutor tradeExecutor, EurUsdRequestor eurUsdRequestor) {
        this.tradeExecutor = tradeExecutor;
        this.strategy = strategySupplier.get(pair);
        this.strategy.setOpenPositionRequestor(tradeExecutor);
        ManagementLoader managementLoader = new ManagementLoader();
        this.entrySignalAdaptor = managementLoader.findEntrySignalAdaptor();
        this.entrySignalValidator = managementLoader.findEntrySignalValidator(CompositeStrategy.ALL_MATCH);
        BrokerRequestor brokerRequestor = BrokerRequestor.find(tradeExecutor);
        this.environmentProvider = managementLoader.findEnvironmentProvider(
                brokerRequestor, brokerRequestor,
                tradeExecutor, tradeExecutor,
                eurUsdRequestor
        );
    }

    public void executeForTime(TimeSeries timeSeries, LocalDateTime time, TradingStrategy strategy) {
        try {
            TimeSeriesEntry currentPrice = timeSeries.getEntryForTime(time);

            adaptPositions(currentPrice);
            closePositionsIfSlOrTpReached(currentPrice);

            shouldExit(new DefaultExitParameter(timeSeries, time, currentPrice), strategy);
            shouldEnter(new DefaultEntryParameter(timeSeries, time, currentPrice), strategy);
        } catch (NoTimeSeriesEntryFoundException e) {
            logger.error("Error while executing Strategy {}", strategy.getClass().getName(), e);
        }
    }

    private void adaptPositions(TimeSeriesEntry currentPrice) {
        tradeExecutor.adaptPositions(currentPrice);
    }

    private void shouldEnter(EntryParameter entryParameter, TradingStrategy strategy) {
        Optional<EntrySignal> entrySignal = strategy.shouldEnter(entryParameter);
        entrySignal
                .map(signal -> entrySignalAdaptor.adapt(environmentProvider, signal))
                .filter(signal -> entrySignalValidator.isValidToExecute(environmentProvider, signal))
                .ifPresent(signal -> tradeExecutor.entry(entryParameter.currentPrice(), entryParameter.timeSeries(), entryParameter.time(), signal));
    }

    private void shouldExit(ExitParameter exitParameter, TradingStrategy strategy) {
        Optional<ExitSignal> exitSignal = strategy.shouldExit(exitParameter);
        exitSignal.ifPresent(signal -> tradeExecutor.exit(exitParameter.currentPrice(), exitParameter.timeSeries(), exitParameter.time(), signal));
    }

    private void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        tradeExecutor.closePositionsIfSlOrTpReached(currentPrice);
    }

    public void update(TimeSeries timeSeries, LocalDateTime time) {
        executeForTime(timeSeries, time, strategy);
    }
}
