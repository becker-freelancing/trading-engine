package com.becker.freelance.execution;

import com.becker.freelance.commons.app.AppConfiguration;

import java.util.List;

public class RemoteExecutionEngine implements Runnable {


    private final AppConfiguration appConfiguration;
    private final List<StrategyWithPair> baseStrategies;

    public RemoteExecutionEngine(List<StrategyWithPair> baseStrategies, AppConfiguration appConfiguration) {
        this.baseStrategies = baseStrategies;
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void run() {
        for (StrategyWithPair baseStrategy : baseStrategies) {
            RemoteExecutionExecutor remoteExecutionExecutor = new RemoteExecutionExecutor(baseStrategy, appConfiguration);
            Thread executionThread = new Thread(remoteExecutionExecutor);
            executionThread.start();
        }

    }
}
