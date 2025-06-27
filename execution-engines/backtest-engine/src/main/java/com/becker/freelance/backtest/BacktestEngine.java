package com.becker.freelance.backtest;

import com.becker.freelance.backtest.callbacks.BacktestResultWriterCallback;
import com.becker.freelance.backtest.commons.BacktestResultWriter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.execution.callback.backtest.BacktestFinishedCallback;
import com.becker.freelance.execution.callback.backtest.BacktestFinishedCallbackComposite;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);


    private final AppConfiguration appConfiguration;
    private final BacktestExecutionConfiguration backtestExecutionConfiguration;
    private final StrategyCreator strategyCreator;
    private final ExecutorService executor;
    private final ParameterFilter parameterFilter;
    private final BacktestFinishedCallback onBacktestFinishedCallback;
    private final Consumer<Exception> onExceptionCallback;
    private final Runnable onFinished;
    private final List<StrategySupplierWithParameters> strategySuppliers;
    private final String strategyName;

    private int currentIteration = 0;
    private int requiredIterations;


    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, ParameterFilter parameterFilter, Path writePath, Runnable onFinished) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                strategyCreator,
                getBacktestResultWriter(appConfiguration, backtestExecutionConfiguration, strategyCreator.strategyName(), writePath),
                parameterFilter,
                onFinished,
                null,
                strategyCreator.strategyName()
        );

    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, Runnable onFinished) {
        this(appConfiguration, backtestExecutionConfiguration, strategyCreator, ParameterFilter.allOkFilter(), null, onFinished);
    }

    protected BacktestEngine(AppConfiguration appConfiguration,
                             BacktestExecutionConfiguration backtestExecutionConfiguration,
                             StrategyCreator strategyCreator,
                             BacktestResultWriter resultWriter,
                             ParameterFilter parameterFilter,
                             Runnable onFinished,
                             List<StrategySupplierWithParameters> supplierWithParameters,
                             String strategyName) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.strategyCreator = strategyCreator;
        this.executor = Executors.newFixedThreadPool(backtestExecutionConfiguration.numberOfThreads());
        this.parameterFilter = parameterFilter;
        this.onBacktestFinishedCallback = new BacktestFinishedCallbackComposite(Set.of(new BacktestResultWriterCallback(resultWriter)));
        this.onExceptionCallback = this::shutdownNowOnException;
        this.onFinished = onFinished;
        this.strategySuppliers = supplierWithParameters;
        this.strategyName = strategyName;
    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, List<StrategySupplierWithParameters> strategySuppliers, Runnable onFinished, String strategyName) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                null,
                getBacktestResultWriter(appConfiguration, backtestExecutionConfiguration, strategyName, null),
                null,
                onFinished,
                strategySuppliers,
                strategyName);
    }

    private static BacktestResultWriter getBacktestResultWriter(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName, Path writePath) {
        final BacktestResultWriter resultWriter;
        if (writePath == null) {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, strategyName);
        } else {
            resultWriter = new BacktestResultWriter(appConfiguration, backtestExecutionConfiguration, writePath);
        }
        return resultWriter;
    }

    public void run() {
        addShutdownHook();
        onBacktestFinishedCallback.initiate(appConfiguration, backtestExecutionConfiguration, strategyName);

        for (StrategySupplierWithParameters strategySupplier : getStrategySupplier()) {

            BacktestExecutor backtestExecutor = new BacktestExecutor(appConfiguration,
                    backtestExecutionConfiguration,
                    onBacktestFinishedCallback,
                    onExceptionCallback,
                    strategySupplier.parameter(),
                    strategySupplier.strategySupplier());

            executor.submit(() -> execute(backtestExecutor));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Could not await termination", e);
        }
        onFinished.run();
    }

    private List<StrategySupplierWithParameters> getStrategySupplier() {
        if (strategySuppliers != null) {
            return strategySuppliers;
        }

        List<StrategyCreationParameter> parameters;
        try (parameterFilter) {
            parameters = strategyCreator.strategyParameters().permutate()
                    .filter(parameterFilter.getPredicate())
                    .asSearchList();
        }
        requiredIterations = parameters.size();

        return parameters.stream().map(this::toStrategySupplier).toList();
    }

    private StrategySupplierWithParameters toStrategySupplier(StrategyCreationParameter parameter) {
        return new StrategySupplierWithParameters((pair, tradingCalculator) -> {
            DefaultStrategyParameter defaultStrategyParameter = new DefaultStrategyParameter(parameter, pair, QuantileMarketRegime.all());
            return strategyCreator.build(defaultStrategyParameter);
        }, parameter);
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

    private void shutdownNowOnException(Exception e) {
        logger.error("Error while executing backtest", e);
        executor.shutdownNow();
    }
}
