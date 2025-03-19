package com.becker.freelance.backtest;

import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);


    private final AppConfiguration appConfiguration;

    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final BaseStrategy baseStrategy;
    private final ExecutorService executor;
    private final BacktestResultWriter resultWriter;
    private final ParameterFilter parameterFilter;
    private final DataProvider dataProvider;
    private int currentIteration = 0;
    private int requiredIterations;
    private Map<Pair, TimeSeries> timeSeries;


    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy, ParameterFilter parameterFilter, Path writePath) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                baseStrategy,
                Executors.newFixedThreadPool(backtestExecutionConfiguration.numberOfThreads()),
                getBacktestResultWriter(appConfiguration, backtestExecutionConfiguration, baseStrategy, writePath),
                parameterFilter,
                DataProvider.getInstance(appConfiguration.appMode())
        );
    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy) {
        this(appConfiguration, backtestExecutionConfiguration, baseStrategy, ParameterFilter.allOkFilter(), null);
    }

    protected BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy, ExecutorService executor, BacktestResultWriter resultWriter, ParameterFilter parameterFilter, DataProvider dataProvider) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.baseStrategy = baseStrategy;
        this.executor = executor;
        this.resultWriter = resultWriter;
        this.parameterFilter = parameterFilter;
        this.dataProvider = dataProvider;
    }

    private static BacktestResultWriter getBacktestResultWriter(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy, Path writePath) {
        final BacktestResultWriter resultWriter;
        if (writePath == null) {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, baseStrategy.getName());
        } else {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, writePath);
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
        timeSeries = backtestExecutionConfiguration.pairs().stream()
                .map(pair -> {
                    try {
                        return dataProvider.readTimeSeries(pair, backtestExecutionConfiguration.startTime(), backtestExecutionConfiguration.endTime());
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .collect(Collectors.toMap(
                        TimeSeries::getPair,
                        t -> t
                ));
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow, "Shutdown-BacktestApp-0"));
    }

    private synchronized int getNextIteration() {
        currentIteration += 1;
        return currentIteration;
    }

    private void executeForParameter(Map<String, Decimal> parameter) {
        try {
            logger.info("Starting Permutation {} of {} - {}", getNextIteration(), this.requiredIterations, parameter);
            TradeExecutor tradeExecutor = TradeExecutor.find(appConfiguration, backtestExecutionConfiguration);

            Supplier<BaseStrategy> strategySupplier = () -> baseStrategy.forParameters(parameter).withOpenPositionRequestor(tradeExecutor);

            StrategyEngine strategyEngine = new StrategyEngine(timeSeries, strategySupplier, tradeExecutor);

            strategyEngine.execute();

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
