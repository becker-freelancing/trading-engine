package com.becker.freelance.backtest;

import com.becker.freelance.commons.app.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteBacktestEngine implements Runnable {


    private static final Logger logger = LoggerFactory.getLogger(RemoteBacktestEngine.class);

    private final AppConfiguration appConfiguration;
    private final List<StrategyWithPair> baseStrategies;
    private final ExecutorService executorService;

    public RemoteBacktestEngine(List<StrategyWithPair> baseStrategies, AppConfiguration appConfiguration) {
        this.baseStrategies = baseStrategies;
        this.appConfiguration = appConfiguration;
        this.executorService = Executors.newFixedThreadPool(baseStrategies.size());
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
