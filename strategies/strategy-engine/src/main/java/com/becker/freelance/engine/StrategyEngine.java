package com.becker.freelance.engine;

import com.becker.freelance.broker.BrokerRequestor;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.PriceRequestor;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.management.api.ManagementLoader;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.environment.TimeChangeListener;
import com.becker.freelance.management.api.validation.CompositeStrategy;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.strategies.executionparameter.DefaultEntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.DefaultExitExecutionParameter;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class StrategyEngine {

    private final Logger logger = LoggerFactory.getLogger(StrategyEngine.class);

    private final TradingStrategy strategy;
    private final TradeExecutor tradeExecutor;
    private final EntrySignalAdaptor entrySignalAdaptor;
    private final EntrySignalValidator entrySignalValidator;
    private final ManagementEnvironmentProvider environmentProvider;

    public StrategyEngine(Pair pair,
                          StrategySupplier strategySupplier,
                          TradeExecutor tradeExecutor,
                          EurUsdRequestor eurUsdRequestor,
                          PriceRequestor priceRequestor,
                          Consumer<TimeChangeListener> timeChangeListenerConsumer,
                          BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator) {
        this.tradeExecutor = tradeExecutor;
        ManagementLoader managementLoader = new ManagementLoader();
        this.entrySignalAdaptor = managementLoader.findEntrySignalAdaptor();
        this.entrySignalValidator = managementLoader.findEntrySignalValidator(CompositeStrategy.ALL_MATCH);
        BrokerRequestor brokerRequestor = BrokerRequestor.find(tradeExecutor);
        this.environmentProvider = managementLoader.findEnvironmentProvider(
                brokerRequestor, brokerRequestor,
                tradeExecutor, tradeExecutor,
                eurUsdRequestor,
                priceRequestor,
                TradingFeeCalculator.fromConfigFile()
        );
        timeChangeListenerConsumer.accept(this.environmentProvider);
        this.strategy = strategySupplier.get(pair, brokerRequestor.getTradingCalculator(eurUsdRequestor));
        this.strategy.setOpenPositionRequestor(tradeExecutor);
        this.strategy.beforeFirstBar(strategyInitiator);
    }

    public void executeForTime(TimeSeries timeSeries, LocalDateTime time, TradingStrategy strategy) {
        try {
            if (environmentProvider.getCurrentAccountBalance().isLessThanZero()){
                return;
            }
            TimeSeriesEntry currentPrice = timeSeries.getEntryForTime(time);

            adaptPositions(currentPrice);
            closePositionsIfSlOrTpReached(currentPrice);

            shouldExit(new DefaultExitExecutionParameter(timeSeries, time, currentPrice), strategy);
            shouldEnter(new DefaultEntryExecutionParameter(timeSeries, time, currentPrice), strategy);
        } catch (Exception e) {
            logger.error("Error while executing Strategy " + strategy.getClass().getName(), e);
        }
    }

    private void adaptPositions(TimeSeriesEntry currentPrice) {
        tradeExecutor.adaptPositions(currentPrice);
    }

    private void shouldEnter(EntryExecutionParameter entryParameter, TradingStrategy strategy) {
        Optional<EntrySignalBuilder> entrySignal = strategy.shouldEnter(entryParameter);
        entrySignal
                .map(signal -> entrySignalAdaptor.adapt(environmentProvider, signal))
                .flatMap(builder -> builder.buildIfValid(entryParameter.currentPrice()))
                .filter(signal -> entrySignalValidator.isValidToExecute(environmentProvider, signal))
                .ifPresent(signal -> tradeExecutor.entry(entryParameter.currentPrice(), entryParameter.timeSeries(), entryParameter.time(), signal));
    }

    private void shouldExit(ExitExecutionParameter exitParameter, TradingStrategy strategy) {
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
