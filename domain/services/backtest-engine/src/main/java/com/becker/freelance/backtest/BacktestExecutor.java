package com.becker.freelance.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.execution.callback.backtest.BacktestFinishedCallback;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleDataSource;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleDataSourceBuilder;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleSourceBuilderParams;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutor;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutorBuildParams;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutorBuilder;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestor;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestorBuilder;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BacktestExecutor implements Runnable {

    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final BacktestFinishedCallback onBacktestFinished;
    private final Consumer<Exception> onError;
    private final StrategyCreationParameter parameters;
    private final StrategySupplier strategySupplier;


    public BacktestExecutor(AppConfiguration appConfiguration,
                            BacktestExecutionConfiguration backtestExecutionConfiguration,
                            BacktestFinishedCallback onBacktestFinished,
                            Consumer<Exception> onError,
                            StrategyCreationParameter parameters,
                            StrategySupplier strategySupplier) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.onBacktestFinished = onBacktestFinished;
        this.onError = onError;
        this.parameters = parameters;
        this.strategySupplier = strategySupplier;
    }

    @Override
    public void run() {
        try {
            ExternalServiceRegistry externalServiceRegistry = ExternalServiceRegistry.newInstance();
            EurUsdRequestor euroUsdRequestor = externalServiceRegistry.requireServiceBuilder(BacktestCandleDataSourceBuilder.class)
                    .createEuroUsdRequestor();
            BacktestTradeExecutorBuilder tradeExecutorBuilder = externalServiceRegistry.requireSupportsServiceBuilder(BacktestTradeExecutorBuilder.class, appConfiguration.appMode());
            BacktestCandleDataSourceBuilder dataProviderFactory = externalServiceRegistry.requireSupportsServiceBuilder(BacktestCandleDataSourceBuilder.class, appConfiguration.appMode());
            AccountBalanceRequestor accountBalanceRequestor = externalServiceRegistry.requireServiceBuilder(AccountBalanceRequestorBuilder.class).build();

            LocalDateTime minTime = backtestExecutionConfiguration.startTime();
            LocalDateTime maxTime = backtestExecutionConfiguration.endTime();
            BacktestSynchronizer backtestSynchronizer = new BacktestSynchronizer(minTime, maxTime, findMaximumTimeShift(backtestExecutionConfiguration.pairs()), new BacktestModeTimeValidator(backtestExecutionConfiguration.backtestMode(), backtestExecutionConfiguration.pairs()));

            List<BacktestTradeExecutor> tradeExecutors = new ArrayList<>();

            for (Pair pair : backtestExecutionConfiguration.pairs()) {
                BacktestTradeExecutor tradeExecutor = tradeExecutorBuilder.build(new BacktestTradeExecutorBuildParams(backtestExecutionConfiguration, pair, euroUsdRequestor));
                tradeExecutors.add(tradeExecutor);

                BacktestCandleDataSource dataProviderForPair = dataProviderFactory.build(new BacktestCandleSourceBuilderParams(pair, backtestSynchronizer));
                Consumer<TimeChangeListener> timeChangeListenerConsumer = listener -> backtestSynchronizer.addPrioritySubscriber(new TimeChangeListenerSynchronizeable(listener));
                BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = getStrategyInitiator(pair, dataProviderForPair);
                StrategyEngine strategyEngine = new StrategyEngine(pair,
                        strategySupplier,
                        tradeExecutor,
                        dataProviderFactory.createEuroUsdRequestor(),
                        dataProviderForPair,
                        timeChangeListenerConsumer,
                        strategyInitiator,
                        accountBalanceRequestor);
                StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine);
                dataProviderForPair.addSubscriber(strategyDataSubscriber);
            }

            while (backtestSynchronizer.getCurrentTime().isBefore(maxTime)) {
                backtestSynchronizer.shiftTime();
            }

            List<Trade> allClosedTrades = tradeExecutors.stream()
                    .map(BacktestTradeExecutor::getAllClosedTrades)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(Trade::getCloseTime))
                    .toList();
            onBacktestFinished.accept(allClosedTrades, parameters);
        } catch (Exception e) {
            onError.accept(e);
        }
    }

    private Duration findMaximumTimeShift(List<Pair> pairs) {
        Long ggt = pairs.stream().map(Pair::toDuration)
                .map(Duration::getSeconds)
                .reduce(0L, this::ggt);

        return Duration.ofSeconds(ggt);
    }

    private long ggt(long n1, long n2) {
        while (n2 != 0) {
            if (n1 > n2) {
                n1 = n1 - n2;
            } else {
                n2 = n2 - n1;
            }
        }
        return n1;
    }

    public StrategyCreationParameter getParameter() {
        return parameters;
    }

    private BiConsumer<TradingStrategy, LocalDateTime> getStrategyInitiator(Pair pair, BacktestCandleDataSource subscribableDataProvider) {
        return (x, y) -> {
        };
//        BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = (tradingStrategy, currentTime) -> {
//            int requiredBarCount = tradingStrategy.unstableBars();
//            Pair strategyPair = tradingStrategy.getPair();
//            long barLengthInMinutes = strategyPair.toDuration().toMinutes();
//            List<TimeSeriesEntry> initiationData = new ArrayList<>();
//            for (int i = 1; i < requiredBarCount; i++) {
//                LocalDateTime requestTime = currentTime.minusMinutes(barLengthInMinutes * i);
//                TimeSeriesEntry priceForTime = subscribableDataProvider.getPriceForTime(strategyPair, requestTime);
//                initiationData.add(priceForTime);
//            }
//            TimeSeries timeSeries = new CompleteTimeSeries(pair, initiationData);
//            tradingStrategy.processInitData(timeSeries);
//        };
//        return strategyInitiator;
    }

}
