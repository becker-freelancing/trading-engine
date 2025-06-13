package com.becker.freelance.backtest;

import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.data.SubscribableDataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.management.api.environment.TimeChangeListener;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class RemoteBacktestExecutor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RemoteBacktestExecutor.class);

    private final StrategySupplier strategySupplier;
    private final Pair pair;
    private final AppConfiguration appConfiguration;

    public RemoteBacktestExecutor(StrategyWithPair baseStrategy, AppConfiguration appConfiguration) {
        this.strategySupplier = baseStrategy.strategySupplier();
        this.pair = baseStrategy.pair();
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void run() {
        try {

            DataProviderFactory dataProviderFactory = DataProviderFactory.find(appConfiguration.appMode());

            TradeExecutor tradeExecutor = TradeExecutor.find(appConfiguration, pair, dataProviderFactory.createEurUsdRequestor());

            SubscribableDataProvider subscribableDataProvider = dataProviderFactory.createSubscribableDataProvider(pair);

            BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = getStrategyInitiator(subscribableDataProvider);


            Set<TimeChangeListener> timeChangeListeners = new HashSet<>();

            StrategyEngine strategyEngine = new StrategyEngine(pair,
                    strategySupplier,
                    tradeExecutor,
                    dataProviderFactory.createEurUsdRequestor(),
                    subscribableDataProvider,
                    timeChangeListeners::add,
                    strategyInitiator);

            StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine, timeChangeListeners);
            subscribableDataProvider.addSubscriber(strategyDataSubscriber);
        } catch (Exception e) {
            logger.error("Error while executing Strategy", e);
        }
    }

    private BiConsumer<TradingStrategy, LocalDateTime> getStrategyInitiator(SubscribableDataProvider subscribableDataProvider) {
        BiConsumer<TradingStrategy, LocalDateTime> strategyInitiator = (tradingStrategy, currentTime) -> {
            int requiredBarCount = tradingStrategy.unstableBars();
            Pair strategyPair = tradingStrategy.getPair();
            long barLengthInMinutes = strategyPair.toDuration().toMinutes();
            List<TimeSeriesEntry> initiationData = new ArrayList<>();
            for (int i = 1; i < requiredBarCount; i++) {
                LocalDateTime requestTime = currentTime.minusMinutes(barLengthInMinutes * i);
                TimeSeriesEntry priceForTime = subscribableDataProvider.getPriceForTime(strategyPair, requestTime);
                initiationData.add(priceForTime);
            }
            TimeSeries timeSeries = new CompleteTimeSeries(pair, initiationData);
            tradingStrategy.processInitData(timeSeries);
        };
        return strategyInitiator;
    }
}
