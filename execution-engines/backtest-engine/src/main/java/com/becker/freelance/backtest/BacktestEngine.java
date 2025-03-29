package com.becker.freelance.backtest;

import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.BaseStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);


    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final BaseStrategy baseStrategy;
    private final ExecutorService executor;
    private final BacktestResultWriter resultWriter;
    private final ParameterFilter parameterFilter;
    private final BiConsumer<List<Trade>, Map<String, Decimal>> onBacktestFinishedCallback;
    private final Consumer<Exception> onExceptionCallback;

    private int currentIteration = 0;
    private int requiredIterations;


    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy, ParameterFilter parameterFilter, Path writePath) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                baseStrategy,
                Executors.newFixedThreadPool(backtestExecutionConfiguration.numberOfThreads()),
                getBacktestResultWriter(appConfiguration, backtestExecutionConfiguration, baseStrategy, writePath),
                parameterFilter
        );

    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy) {
        this(appConfiguration, backtestExecutionConfiguration, baseStrategy, ParameterFilter.allOkFilter(), null);
    }

    protected BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, BaseStrategy baseStrategy, ExecutorService executor, BacktestResultWriter resultWriter, ParameterFilter parameterFilter) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.baseStrategy = baseStrategy;
        this.executor = executor;
        this.resultWriter = resultWriter;
        this.parameterFilter = parameterFilter;
        this.onBacktestFinishedCallback = this::writeBacktestResult;
        this.onExceptionCallback = this::shutdownNowOnException;
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
        addShutdownHook();
        List<Map<String, Decimal>> parameters;
        try (parameterFilter) {
            parameters = baseStrategy.getParameters().permutate()
                    .stream().filter(parameterFilter.getPredicate()).toList();
        }
        requiredIterations = parameters.size();
        for (Map<String, Decimal> parameter : parameters) {
            Supplier<BaseStrategy> strategySupplier = () -> baseStrategy.forParameters(parameter);

            BacktestExecutor backtestExecutor = new BacktestExecutor(appConfiguration,
                    backtestExecutionConfiguration,
                    onBacktestFinishedCallback,
                    onExceptionCallback,
                    parameter,
                    strategySupplier);

            executor.submit(() -> execute(backtestExecutor));
        }

        executor.shutdown();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow, "Shutdown-BacktestApp-0"));
    }

    private synchronized int getNextIteration() {
        currentIteration += 1;
        return currentIteration;
    }

    private void execute(BacktestExecutor backtestExecutor) {
        logger.info("Starting Permutation {} of {} - {}", getNextIteration(), this.requiredIterations, backtestExecutor.getParameter());
        backtestExecutor.run();
    }

    private void writeBacktestResult(List<Trade> allClosedTrades, Map<String, Decimal> parameter) {
        try {
            resultWriter.writeResult(allClosedTrades, parameter);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void shutdownNowOnException(Exception e) {
        logger.error("Error while executing backtest", e);
        executor.shutdownNow();
    }
}
