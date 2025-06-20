package com.becker.freelance.backtest;

import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);


    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final StrategyCreator strategyCreator;
    private final ExecutorService executor;
    private final BacktestResultWriter resultWriter;
    private final ParameterFilter parameterFilter;
    private final BiConsumer<List<Trade>, StrategyCreationParameter> onBacktestFinishedCallback;
    private final Consumer<Exception> onExceptionCallback;
    private final Runnable onFinished;

    private int currentIteration = 0;
    private int requiredIterations;


    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, ParameterFilter parameterFilter, Path writePath, Runnable onFinished) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                strategyCreator,
                Executors.newFixedThreadPool(backtestExecutionConfiguration.numberOfThreads()),
                getBacktestResultWriter(appConfiguration, backtestExecutionConfiguration, strategyCreator, writePath),
                parameterFilter,
                onFinished
        );

    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, Runnable onFinished) {
        this(appConfiguration, backtestExecutionConfiguration, strategyCreator, ParameterFilter.allOkFilter(), null, onFinished);
    }

    protected BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, ExecutorService executor, BacktestResultWriter resultWriter, ParameterFilter parameterFilter, Runnable onFinished) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.strategyCreator = strategyCreator;
        this.executor = executor;
        this.resultWriter = resultWriter;
        this.parameterFilter = parameterFilter;
        this.onBacktestFinishedCallback = this::writeBacktestResult;
        this.onExceptionCallback = this::shutdownNowOnException;
        this.onFinished = onFinished;
    }

    private static BacktestResultWriter getBacktestResultWriter(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator baseStrategy, Path writePath) {
        final BacktestResultWriter resultWriter;
        if (writePath == null) {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, baseStrategy.strategyName());
        } else {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, writePath);
        }
        return resultWriter;
    }

    public void run() {
        addShutdownHook();
        List<StrategyCreationParameter> parameters;
        try (parameterFilter) {
            parameters = strategyCreator.strategyParameters().permutate()
                    .filter(parameterFilter.getPredicate())
                    .asSearchList();
        }
        requiredIterations = parameters.size();
        for (StrategyCreationParameter parameter : parameters) {
            StrategySupplier strategySupplier = (pair, tradingCalculator) -> {
                DefaultStrategyParameter defaultStrategyParameter = new DefaultStrategyParameter(parameter, pair, QuantileMarketRegime.all());
                return strategyCreator.build(defaultStrategyParameter);
            };

            BacktestExecutor backtestExecutor = new BacktestExecutor(appConfiguration,
                    backtestExecutionConfiguration,
                    onBacktestFinishedCallback,
                    onExceptionCallback,
                    parameter,
                    strategySupplier);

            executor.submit(() -> execute(backtestExecutor));
        }

        executor.shutdown();
        onFinished.run();
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

    private void writeBacktestResult(List<Trade> allClosedTrades, StrategyCreationParameter parameter) {
        try {
            resultWriter.writeResult(allClosedTrades, parameter.asMap());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void shutdownNowOnException(Exception e) {
        logger.error("Error while executing backtest", e);
        executor.shutdownNow();
    }
}
