package com.becker.freelance.backtest;

import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.ExecutionConfiguration;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.engine.StrategyEngine;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;
import com.becker.freelance.tradeexecution.TradeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);


    private final TradeExecutor tradeExecutor;

    private final ExecutionConfiguration executionConfiguration;
    private final BaseStrategy baseStrategy;
    private final ExecutorService executor;
    private final BacktestResultWriter resultWriter;
    private final ParameterFilter parameterFilter;
    private final DataProvider dataProvider;
    public BacktestEngine(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy, ParameterFilter parameterFilter, Path writePath) {
        this(executionConfiguration, baseStrategy, Executors.newFixedThreadPool(appConfiguration.numThreads()),
                getBacktestResultWriter(appConfiguration, executionConfiguration, baseStrategy, writePath), parameterFilter,
                TradeExecutor.find(appConfiguration, executionConfiguration),
                DataProvider.getInstance(appConfiguration.appMode())
        );
    }
    private int currentIteration = 0;
    private int requiredIterations;
    private TimeSeries timeSeries;

    public BacktestEngine(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy) {
        this(appConfiguration, executionConfiguration, baseStrategy, ParameterFilter.allOkFilter(), null);
    }

    protected BacktestEngine(ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy, ExecutorService executor, BacktestResultWriter resultWriter, ParameterFilter parameterFilter, TradeExecutor tradeExecutor, DataProvider dataProvider) {
        this.executionConfiguration = executionConfiguration;
        this.baseStrategy = baseStrategy;
        this.executor = executor;
        this.resultWriter = resultWriter;
        this.parameterFilter = parameterFilter;
        this.tradeExecutor = tradeExecutor;
        this.dataProvider = dataProvider;
    }

    private static BacktestResultWriter getBacktestResultWriter(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy, Path writePath) {
        final BacktestResultWriter resultWriter;
        if (writePath == null) {
            resultWriter = new BacktestResultWriter(appConfiguration, executionConfiguration, baseStrategy);
        } else {
            resultWriter = new BacktestResultWriter(appConfiguration, executionConfiguration, writePath);
        }
        return resultWriter;
    }

    public void run() {
        init();
        List<Map<String, Decimal>> parameters;
        try (parameterFilter) {
            parameters = baseStrategy.getParameters().permutate()
                    .stream().filter(parameterFilter.getPredicate()).toList();
        }
        requiredIterations = parameters.size();
        for (Map<String, Decimal> parameter : parameters) {
            executor.submit(() -> executeForParameter(parameter));
        }

        executor.shutdown();
    }

    private void init() {
        try {
            timeSeries = dataProvider.readTimeSeries(executionConfiguration.pair(), executionConfiguration.startTime(), executionConfiguration.endTime());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow, "Shutdown-BacktestApp-0"));
    }

    private synchronized int getNextIteration() {
        currentIteration += 1;
        return currentIteration;
    }

    private void executeForParameter(Map<String, Decimal> parameter) {
        try {
            logger.info("Starting Permutation {} of {} - {}", getNextIteration(), this.requiredIterations, parameter);
            BaseStrategy strategyForBacktest = baseStrategy.forParameters(parameter);

            StrategyEngine strategyEngine = new StrategyEngine(strategyForBacktest, tradeExecutor);

            for (LocalDateTime timeKey : timeSeries.iterator(executionConfiguration.startTime(), executionConfiguration.endTime())) {
                strategyEngine.executeForTime(timeSeries, timeKey);
            }

            List<Trade> allClosedTrades = tradeExecutor.getAllClosedTrades();
            try {
                resultWriter.writeResult(allClosedTrades, parameter);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } catch (Exception e) {
            logger.error("Error while executing backtest", e);
            executor.shutdownNow();
        }
    }
}
