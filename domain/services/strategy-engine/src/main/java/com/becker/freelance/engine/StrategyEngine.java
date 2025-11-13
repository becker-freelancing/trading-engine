package com.becker.freelance.engine;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.strategies.executionparameter.DefaultEntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.DefaultExitExecutionParameter;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestor;
import com.becker.freelance.trading.external.services.broker.BrokerSpecificsRequestor;
import com.becker.freelance.trading.external.services.broker.BrokerSpecificsRequestorBuilder;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBroker;
import com.becker.freelance.trading.external.services.fees.TradingFeeCalculator;
import com.becker.freelance.trading.external.services.fees.TradingFeeCalculatorBuilder;
import com.becker.freelance.trading.external.services.fees.TradingFeeCalculatorBuilderParams;
import com.becker.freelance.trading.external.services.management.adaption.EntrySignalAdaptor;
import com.becker.freelance.trading.external.services.management.adaption.EntrySignalAdaptorBuilder;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProvider;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProviderBuilder;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProviderBuilderParams;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;
import com.becker.freelance.trading.external.services.management.validation.CompositeStrategy;
import com.becker.freelance.trading.external.services.management.validation.EntrySignalValidator;
import com.becker.freelance.trading.external.services.management.validation.EntrySignalValidatorBuilder;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
import com.becker.freelance.trading.external.services.registry.ScopedExternalServiceRegistry;
import com.becker.freelance.trading.external.services.strategies.MissingDataHandler;
import com.becker.freelance.trading.external.services.strategies.MissingDataHandlerBuilder;
import com.becker.freelance.trading.external.services.strategies.MissingDataHandlerBuilderParams;
import com.becker.freelance.trading.external.services.tradeexecution.TradeExecutor;
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
    private final MissingDataHandler missingDataHandler;

    public StrategyEngine(Pair pair,
                          StrategySupplier strategySupplier,
                          TradeExecutor tradeExecutor,
                          EurUsdRequestor eurUsdRequestor,
                          PriceRequestorBroker priceRequestorBroker,
                          Consumer<TimeChangeListener> timeChangeListenerConsumer,
                          BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator,
                          AccountBalanceRequestor accountBalanceRequestor,
                          ScopedExternalServiceRegistry scopedExternalServiceRegistry) {
        this.tradeExecutor = tradeExecutor;

        ExternalServiceRegistry externalServiceRegistry = ExternalServiceRegistry.globalServiceRegistry();
        this.entrySignalAdaptor = externalServiceRegistry.requireServiceBuilder(EntrySignalAdaptorBuilder.class).build();
        this.entrySignalValidator = externalServiceRegistry.requireServiceBuilder(EntrySignalValidatorBuilder.class).build(CompositeStrategy.ALL_MATCH);
        BrokerSpecificsRequestor brokerSpecificsRequestor = externalServiceRegistry.requireServiceBuilder(BrokerSpecificsRequestorBuilder.class).build();
        TradingFeeCalculator tradingFeeCalculator = externalServiceRegistry.requireServiceBuilder(TradingFeeCalculatorBuilder.class).build(new TradingFeeCalculatorBuilderParams(
                priceRequestorBroker,
                eurUsdRequestor
        ));
        this.environmentProvider = externalServiceRegistry.requireServiceBuilder(ManagementEnvironmentProviderBuilder.class)
                .build(new ManagementEnvironmentProviderBuilderParams(
                        accountBalanceRequestor,
                        brokerSpecificsRequestor,
                        tradeExecutor,
                        tradeExecutor,
                        eurUsdRequestor,
                        priceRequestorBroker,
                        tradingFeeCalculator
                ));
        timeChangeListenerConsumer.accept(this.environmentProvider);
        this.strategy = strategySupplier.get(pair, brokerSpecificsRequestor.getTradingCalculator(eurUsdRequestor), scopedExternalServiceRegistry);
        this.strategy.setOpenPositionRequestor(tradeExecutor);
        this.strategy.beforeFirstBar(strategyInitiator);

        this.missingDataHandler = externalServiceRegistry.requireServiceBuilder(MissingDataHandlerBuilder.class).build(new MissingDataHandlerBuilderParams(pair));
    }

    private void executeForTime(TimeSeries timeSeries, LocalDateTime time, TradingStrategy strategy) {
        try {
            if (environmentProvider.getCurrentAccountBalance().isLessThanZero()) {
                return;
            }
            TimeSeriesEntry currentPrice = timeSeries.getEntryForTime(time);

            adaptPositions(currentPrice);
            closePositionsIfSlOrTpReached(currentPrice);

            if (missingDataHandler.shouldResetStrategy()) {
                strategy.reset();
                return;
            }

            shouldExit(new DefaultExitExecutionParameter(timeSeries, time, currentPrice), strategy);
            shouldEnter(new DefaultEntryExecutionParameter(timeSeries, time, currentPrice), strategy);
        } catch (Exception e) {
            logger.error("Error while executing Strategy {}", strategy.getClass().getName(), e);
            logger.warn("Proceeding strategy execution after error {}", e.getMessage());
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
        missingDataHandler.onDataReceived(time);
    }

    public void onMissingData(LocalDateTime time) {
        missingDataHandler.onMissingData(time);
    }
}
