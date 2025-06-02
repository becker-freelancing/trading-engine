package com.becker.freelance.app;

import com.becker.freelance.backtest.RemoteBacktestEngine;
import com.becker.freelance.backtest.StrategyWithPair;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.strategies.creation.StrategyCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AbstractRemoteBacktestApp implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(AbstractRemoteBacktestApp.class);

    private final RemoteBacktestAppInitiatingUtil appInitiatingUtil;

    AbstractRemoteBacktestApp() {
        this.appInitiatingUtil = new RemoteBacktestAppInitiatingUtil();
    }

    @Override
    public void run() {
        List<StrategyCreator> strategies = appInitiatingUtil.askStrategy();
        logger.info("Strategies in Backtest: {}", strategies.stream().map(StrategyCreator::strategyName).toList());

        AppMode appMode = appInitiatingUtil.askAppMode();
        logger.info("AppMode: {}", appMode.getDescription());

        List<Pair> pairs = appInitiatingUtil.askPair(appMode);
        logger.info("Pairs: {}", pairs.stream().map(Pair::technicalName).toList());

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        List<StrategyWithPair> initiatedStrategiesForPairs = strategies.stream()
                .map(this::toSupplier)
                .map(strategySupplier -> pairs.stream().map(pair -> new StrategyWithPair(strategySupplier, pair)).toList())
                .flatMap(List::stream)
                .toList();

        RemoteBacktestEngine remoteBacktestEngine = new RemoteBacktestEngine(initiatedStrategiesForPairs, appConfiguration);
        remoteBacktestEngine.run();
    }

    private StrategySupplier toSupplier(StrategyCreator strategy) {
        return (pair) -> strategy.build(pair, strategy.strategyParameters().defaultValues());
    }
}
