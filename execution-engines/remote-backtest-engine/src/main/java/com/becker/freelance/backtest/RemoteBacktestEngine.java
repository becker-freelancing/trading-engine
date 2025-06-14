package com.becker.freelance.backtest;

import com.becker.freelance.commons.app.AppConfiguration;

import java.util.List;

public class RemoteBacktestEngine implements Runnable {


    private final AppConfiguration appConfiguration;
    private final List<StrategyWithPair> baseStrategies;

    public RemoteBacktestEngine(List<StrategyWithPair> baseStrategies, AppConfiguration appConfiguration) {
        this.baseStrategies = baseStrategies;
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void run() {
        for (StrategyWithPair baseStrategy : baseStrategies) {
            RemoteBacktestExecutor remoteBacktestExecutor = new RemoteBacktestExecutor(baseStrategy, appConfiguration);
            Thread executionThread = new Thread(remoteBacktestExecutor);
            executionThread.start();
        }

    }
}
