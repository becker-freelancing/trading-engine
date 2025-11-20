package com.becker.freelance.backtest;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.configuration.BacktestStage;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.indicators.ta.regime.TradeableMarketRegimeWrapper;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import com.becker.freelance.strategies.validinitparameter.ValidStrategyInitParameters;
import com.becker.freelance.trading.external.services.backtest.callbacks.BacktestFinishedCallback;
import com.becker.freelance.trading.external.services.backtest.callbacks.BacktestFinishedCallbackBuilder;
import com.becker.freelance.trading.external.services.backtest.callbacks.BacktestFinishedCallbackBuilderParams;
import com.becker.freelance.trading.external.services.backtest.strategies.ParameterFilter;
import com.becker.freelance.trading.external.services.backtest.strategies.StrategyParamsSampler;
import com.becker.freelance.trading.external.services.backtest.strategies.StrategyParamsSamplerBuilder;
import com.becker.freelance.trading.external.services.backtest.strategies.StrategyParamsSamplerBuilderParams;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
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
    private final Consumer<Throwable> onExceptionCallback;
    private final Runnable onFinished;
    private final List<StrategySupplierWithParameters> strategySuppliers;
    private final String strategyName;

    private int currentIteration = 0;
    private int requiredIterations;


    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategyCreator, ParameterFilter parameterFilter, Path writePath, Runnable onFinished) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                strategyCreator,
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
                             ParameterFilter parameterFilter,
                             Runnable onFinished,
                             List<StrategySupplierWithParameters> supplierWithParameters,
                             String strategyName) {
        this.appConfiguration = appConfiguration;
        this.backtestExecutionConfiguration = backtestExecutionConfiguration;
        this.strategyCreator = strategyCreator;
        this.executor = Executors.newFixedThreadPool(backtestExecutionConfiguration.numberOfThreads());
        this.parameterFilter = parameterFilter;
        this.onBacktestFinishedCallback = ExternalServiceRegistry.globalServiceRegistry().requireServiceBuilder(BacktestFinishedCallbackBuilder.class).build(new BacktestFinishedCallbackBuilderParams(backtestExecutionConfiguration.pairs(), strategyName, appConfiguration.applicationStartTime()));
        this.onExceptionCallback = this::shutdownNowOnException;
        this.onFinished = onFinished;
        this.strategySuppliers = supplierWithParameters;
        this.strategyName = strategyName;
    }

    public BacktestEngine(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, List<StrategySupplierWithParameters> strategySuppliers, Runnable onFinished, String strategyName) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                null,
                null,
                onFinished,
                strategySuppliers,
                strategyName);
    }

    public void run() {
        addShutdownHook();
        onBacktestFinishedCallback.initiate(appConfiguration, backtestExecutionConfiguration, strategyName);

        List<StrategySupplierWithParameters> strategySuppliers = getStrategySupplier(backtestExecutionConfiguration.backtestStage());
        for (int i = 0; i < strategySuppliers.size(); i++) {

            StrategySupplierWithParameters strategySupplier = strategySuppliers.get(i);

            BacktestExecutor backtestExecutor = new BacktestExecutor(
                    new Decimal(i),
                    appConfiguration,
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

    private List<StrategySupplierWithParameters> getStrategySupplier(BacktestStage backtestStage) {
        logger.info("Creating strategy suppliers...");
        if (strategySuppliers != null) {
            return strategySuppliers;
        }

        List<StrategyCreationParameter> parameters;
        try (parameterFilter) {
            StrategyParamsSampler paramsSampler = ExternalServiceRegistry.globalServiceRegistry()
                    .requireSupportsServiceBuilder(StrategyParamsSamplerBuilder.class, backtestStage)
                    .build(new StrategyParamsSamplerBuilderParams(
                            backtestExecutionConfiguration.parameterLimit()
                    ));

            ValidStrategyInitParameters validStrategyInitParameters = strategyCreator.strategyParameters();
            parameters = paramsSampler.sample(validStrategyInitParameters.getStrategyInitParameter(), validStrategyInitParameters.getParameterValidation()).stream()
                    .filter(parameterFilter.getPredicate())
                    .toList();
        }
        requiredIterations = parameters.size();

        return parameters.stream().map(this::toStrategySupplier).toList();
    }

    private StrategySupplierWithParameters toStrategySupplier(StrategyCreationParameter parameter) {
        return new StrategySupplierWithParameters((pair, tradingCalculator, scopedExternalServiceRegistry) -> {
            DefaultStrategyParameter defaultStrategyParameter = new DefaultStrategyParameter(parameter,
                    pair,
                    TradeableMarketRegimeWrapper.all(),
                    scopedExternalServiceRegistry);
            return strategyCreator.build(defaultStrategyParameter);
        }, parameter);
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            onBacktestFinishedCallback.onAllFinished();
            executor.shutdownNow();
        }, "Shutdown-BacktestApp-0"));
    }

    private synchronized int getNextIteration() {
        currentIteration += 1;
        return currentIteration;
    }

    private void execute(BacktestExecutor backtestExecutor) {
        logger.info("Starting Permutation {} of {} - {}", getNextIteration(), this.requiredIterations, backtestExecutor.getParameter());
        backtestExecutor.run();
    }

    private void shutdownNowOnException(Throwable e) {
        logger.error("Error while executing backtest", e);
        executor.shutdownNow();
    }
}
