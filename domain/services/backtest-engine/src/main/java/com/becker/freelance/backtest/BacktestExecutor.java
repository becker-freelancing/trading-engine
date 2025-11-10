package com.becker.freelance.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.wallet.BacktestWallet;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.trading.external.services.backtest.broker.BacktestAccountBalanceRequestor;
import com.becker.freelance.trading.external.services.backtest.broker.BacktestAccountBalanceRequestorBuilder;
import com.becker.freelance.trading.external.services.backtest.callbacks.BacktestFinishedCallback;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleDataSource;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleDataSourceBuilder;
import com.becker.freelance.trading.external.services.backtest.candles.BacktestCandleSourceBuilderParams;
import com.becker.freelance.trading.external.services.backtest.earlystop.EarlyStopCallbackResult;
import com.becker.freelance.trading.external.services.backtest.earlystop.NoStopEarlyStopCallbackResult;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutor;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutorBuildParams;
import com.becker.freelance.trading.external.services.backtest.tradeexecution.BacktestTradeExecutorBuilder;
import com.becker.freelance.trading.external.services.management.environment.TimeChangeListener;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
import com.becker.freelance.trading.external.services.registry.ScopedExternalServiceRegistry;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BacktestExecutor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BacktestExecutor.class);

    private final Decimal executionId;
    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final BacktestFinishedCallback onBacktestFinished;
    private final Consumer<Exception> onError;
    private final StrategyCreationParameter parameters;
    private final StrategySupplier strategySupplier;


    public BacktestExecutor(Decimal executionId, AppConfiguration appConfiguration,
                            BacktestExecutionConfiguration backtestExecutionConfiguration,
                            BacktestFinishedCallback onBacktestFinished,
                            Consumer<Exception> onError,
                            StrategyCreationParameter parameters,
                            StrategySupplier strategySupplier) {
        this.executionId = executionId;
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
            ExternalServiceRegistry externalServiceRegistry = ExternalServiceRegistry.globalServiceRegistry();
            ScopedExternalServiceRegistry scopedExternalServiceRegistry = externalServiceRegistry.newScopedExternalServiceRegistry();

            BacktestTradeExecutorBuilder tradeExecutorBuilder = externalServiceRegistry.requireSupportsServiceBuilder(BacktestTradeExecutorBuilder.class, appConfiguration.appMode());
            BacktestCandleDataSourceBuilder dataProviderFactory = externalServiceRegistry.requireSupportsServiceBuilder(BacktestCandleDataSourceBuilder.class, appConfiguration.appMode());

            LocalDateTime minTime = backtestExecutionConfiguration.startTime();
            LocalDateTime maxTime = backtestExecutionConfiguration.endTime();
            BacktestSynchronizer backtestSynchronizer = new BacktestSynchronizer(minTime, maxTime,
                    findMaximumTimeShift(backtestExecutionConfiguration.pairs()),
                    new BacktestModeTimeValidator(backtestExecutionConfiguration.backtestMode(), backtestExecutionConfiguration.pairs()));

            scopedExternalServiceRegistry.registerScopedExternalService(backtestSynchronizer);

            EurUsdRequestor euroUsdRequestor = externalServiceRegistry.requireServiceBuilder(BacktestCandleDataSourceBuilder.class)
                    .createEuroUsdRequestor(backtestSynchronizer);

            List<BacktestTradeExecutor> tradeExecutors = new ArrayList<>();

            EarlyStopCallbackImpl earlyStopCallback = new EarlyStopCallbackImpl(backtestExecutionConfiguration.initialWalletAmount());
            BacktestWallet wallet = new BacktestWallet(backtestExecutionConfiguration.initialWalletAmount());
            Supplier<Wallet> walletSupplier = () -> wallet;
            for (Pair pair : backtestExecutionConfiguration.pairs()) {
                BacktestTradeExecutor tradeExecutor = tradeExecutorBuilder.build(new BacktestTradeExecutorBuildParams(backtestExecutionConfiguration, pair, euroUsdRequestor));
                tradeExecutor.addClosedTradeSubscriber(earlyStopCallback);
                tradeExecutor.setWallet(walletSupplier);
                tradeExecutors.add(tradeExecutor);
                BacktestAccountBalanceRequestor accountBalanceRequestor = externalServiceRegistry.requireServiceBuilder(BacktestAccountBalanceRequestorBuilder.class).build();
                accountBalanceRequestor.setWallet(wallet);

                BacktestCandleDataSource dataProviderForPair = dataProviderFactory.build(new BacktestCandleSourceBuilderParams(pair, backtestSynchronizer));
                Consumer<TimeChangeListener> timeChangeListenerConsumer = listener -> backtestSynchronizer.addPrioritySubscriber(new TimeChangeListenerSynchronizeable(listener));
                BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = getStrategyInitiator(pair, dataProviderForPair);
                StrategyEngine strategyEngine = new StrategyEngine(pair,
                        strategySupplier,
                        tradeExecutor,
                        euroUsdRequestor,
                        dataProviderForPair,
                        timeChangeListenerConsumer,
                        strategyInitiator,
                        accountBalanceRequestor,
                        scopedExternalServiceRegistry);
                StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine);
                dataProviderForPair.addSubscriber(strategyDataSubscriber);
            }

            EarlyStopCallbackResult earlyStopCallbackResult = new NoStopEarlyStopCallbackResult();

            while (!earlyStopCallbackResult.shouldStop() && backtestSynchronizer.getCurrentTime().isBefore(maxTime)) {
                backtestSynchronizer.shiftTime();
                earlyStopCallbackResult = earlyStopCallback.shouldStop();
            }

            List<Trade> allClosedTrades = tradeExecutors.stream()
                    .map(BacktestTradeExecutor::getAllClosedTrades)
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(Trade::getCloseTime))
                    .toList();
            onBacktestFinished.accept(executionId, allClosedTrades, parameters, earlyStopCallbackResult);
        } catch (Exception e) {
            onError.accept(e);
        }
    }

    private Duration findMaximumTimeShift(List<Pair> pairs) {
        if (pairs.isEmpty()) {
            throw new IllegalStateException("Can not find maximum shift time if no pairs are provided");
        }
        Long ggt = pairs.stream().map(Pair::toDuration)
                .map(Duration::getSeconds)
                .reduce(pairs.get(0).toDuration().getSeconds(), this::ggt);

        Duration timeShift = Duration.ofSeconds(ggt);
        logger.info("Using time shift: {}", timeShift);
        return timeShift;
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
