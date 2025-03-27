package com.becker.freelance.backtest;

import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.data.SubscribableDataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RemoteBacktestExecutor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RemoteBacktestExecutor.class);

    private final Supplier<BaseStrategy> strategySupplier;
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
            TradeExecutor tradeExecutor = TradeExecutor.find(appConfiguration, pair);

            DataProviderFactory dataProviderFactory = DataProviderFactory.find(appConfiguration.appMode());

            SubscribableDataProvider subscribableDataProvider = dataProviderFactory.createSubscribableDataProvider(pair);

            StrategyEngine strategyEngine = new StrategyEngine(strategySupplier, tradeExecutor);
            StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine);
            subscribableDataProvider.addSubscriber(strategyDataSubscriber);
        } catch (Exception e) {
            logger.error("Error for Strategy {}", strategySupplier.get().getName(), e);
        }
    }
}
