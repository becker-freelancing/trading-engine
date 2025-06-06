package com.becker.freelance.backtest;

import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.data.SubscribableDataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            if (true) {
                throw new UnsupportedOperationException("Management Provider muss Uhrzeitabhängig gemacht werden, evtl.");
            }
            StrategyEngine strategyEngine = new StrategyEngine(pair, strategySupplier, tradeExecutor, dataProviderFactory.createEurUsdRequestor(), listener -> {
            });
            StrategyDataSubscriber strategyDataSubscriber = new StrategyDataSubscriber(strategyEngine);
            subscribableDataProvider.addSubscriber(strategyDataSubscriber);
        } catch (Exception e) {
            logger.error("Error while executing Strategy", e);
        }
    }
}
