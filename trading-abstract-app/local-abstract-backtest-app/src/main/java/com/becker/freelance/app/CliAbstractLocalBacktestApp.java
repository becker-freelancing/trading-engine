package com.becker.freelance.app;

import com.becker.freelance.app.BacktestAppInitiatingUtil.LastExecutionProperties;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.StrategyCreator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class CliAbstractLocalBacktestApp extends AbstractLocalBacktestApp {


    private final BacktestAppInitiatingUtil appInitiatingUtil;
    private StrategyCreator strategy;
    private AppMode appMode;
    private List<Pair> pairs;
    private Integer numThreads;

    CliAbstractLocalBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, Runnable onFinished, boolean strategyConfig) {
        super(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig);
        this.appInitiatingUtil = new BacktestAppInitiatingUtil();
    }

    @Override
    protected void initiate() {
        Optional<LastExecutionProperties> properties = appInitiatingUtil.findProperties();
        if (properties.isPresent()) {
            LastExecutionProperties lastExecutionProperties = properties.get();
            strategy = lastExecutionProperties.baseStrategy();
            appMode = lastExecutionProperties.appMode();
            pairs = lastExecutionProperties.pairs();
            numThreads = lastExecutionProperties.numberOfThread();
        } else {
            strategy = appInitiatingUtil.askStrategy();
            appMode = appInitiatingUtil.askAppMode();
            pairs = appInitiatingUtil.askPair(appMode);
            numThreads = appInitiatingUtil.askNumberOfThreads();
        }
        appInitiatingUtil.saveProperties(strategy, numThreads, pairs, appMode);
    }

    @Override
    protected StrategyCreator getStrategyCreator() {
        return strategy;
    }

    @Override
    protected AppMode getAppMode() {
        return appMode;
    }

    @Override
    protected List<Pair> getPairs() {
        return pairs;
    }

    @Override
    protected Integer getNumThreads() {
        return numThreads;
    }

}
