package com.becker.freelance.app;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.StrategyCreator;

import java.time.LocalDateTime;
import java.util.List;

class ConfiguredAbstractLocalBacktestApp extends AbstractLocalBacktestApp {


    private final String strategy;
    private final String appMode;
    private final List<String> pairs;
    private final Integer numThreads;

    ConfiguredAbstractLocalBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, Runnable onFinished, boolean strategyConfig, String strategy, String appMode, List<String> pairs, Integer numThreads) {
        super(initialWalletAmount, fromTime, toTime, onFinished, strategyConfig);
        this.strategy = strategy;
        this.appMode = appMode;
        this.pairs = pairs;
        this.numThreads = numThreads;
    }

    @Override
    protected void initiate() {

    }

    @Override
    protected StrategyCreator getStrategyCreator() {
        return StrategyCreator.findAll().stream()
                .filter(strategyCreator -> strategyCreator.strategyName().equals(strategy))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    protected AppMode getAppMode() {
        return AppMode.findAll().stream()
                .filter(mode -> mode.getDescription().equals(appMode))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    protected List<Pair> getPairs() {
        AppMode mode = getAppMode();
        return Pair.allPairs().stream()
                .filter(pair -> pair.isExecutableInAppMode(mode))
                .filter(pair -> pairs.contains(pair.shortName()))
                .toList();
    }

    @Override
    protected Integer getNumThreads() {
        return numThreads;
    }

}
