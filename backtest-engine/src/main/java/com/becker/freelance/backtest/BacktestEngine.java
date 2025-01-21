package com.becker.freelance.backtest;

import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.ExecutionConfiguration;
import com.becker.freelance.commons.TimeSeries;
import com.becker.freelance.commons.Trade;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);

    private final AppConfiguration appConfiguration;
    private final ExecutionConfiguration executionConfiguration;
    private final BaseStrategy baseStrategy;
    private final ExecutorService executor;
    private final BacktestResultWriter resultWriter;
    private int currentIteration = 0;
    private int requiredIterations;
    private TimeSeries timeSeries;

    public BacktestEngine(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy) {
        this.appConfiguration = appConfiguration;
        this.executionConfiguration = executionConfiguration;
        this.baseStrategy = baseStrategy;
        this.executor = Executors.newFixedThreadPool(appConfiguration.getNumThreads());
        resultWriter = new BacktestResultWriter(appConfiguration, executionConfiguration, baseStrategy);
    }

    public void run() {
        init();
        List<Map<String, Double>> parameters = baseStrategy.getParameters().permutate();
        requiredIterations = parameters.size();
        for (Map<String, Double> parameter : parameters) {
            executor.submit(() -> executeForParameter(parameter));
        }

        executor.shutdown();
    }

    private void init() {
        try {
            timeSeries = DataProvider.getInstance(appConfiguration.getAppMode())
                    .readTimeSeries(executionConfiguration.getPair(), executionConfiguration.getStartTime(), executionConfiguration.getEndTime());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private synchronized int getNextIteration(){
        currentIteration += 1;
        return currentIteration;
    }

    private void executeForParameter(Map<String, Double> parameter) {
        try {
            logger.info("Starting Permutation {} of {} - {}", getNextIteration(), this.requiredIterations, parameter);
            BaseStrategy strategyForBacktest = baseStrategy.forParameters(parameter);
            TradeExecutor tradeExecutor = TradeExecutor.find(appConfiguration, executionConfiguration);

            StrategyEngine strategyEngine = new StrategyEngine(strategyForBacktest, tradeExecutor);

            for (LocalDateTime timeKey : timeSeries.iterator(executionConfiguration.getStartTime(), executionConfiguration.getEndTime())) {
                strategyEngine.executeForTime(timeSeries, timeKey);
            }

            List<Trade> allClosedTrades = tradeExecutor.getAllClosedTrades();
            try {
                resultWriter.writeResult(allClosedTrades, parameter);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } catch (Exception e){
            logger.error("Error while executing backtest", e);
            executor.shutdownNow();
        }
    }
}
