package com.becker.freelance.trading.application;

import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.backtest.ExcludeExistingParametersFilter;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.trading.api.LocalBacktestPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class LocalBacktestContinueInteractor implements LocalBacktestPort {

    private static final Logger logger = LoggerFactory.getLogger(LocalBacktestContinueInteractor.class);

    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final StrategyCreator strategy;
    private final AppMode appMode;
    private final List<Pair> pairs;
    private final Integer numThreads;
    private final Set<StrategyCreationParameter> parameters;
    private final Path resultWriteFile;

    public LocalBacktestContinueInteractor(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, StrategyCreator strategy, AppMode appMode, List<Pair> pairs, Integer numThreads, Set<StrategyCreationParameter> parameters, Path resultWriteFile) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.strategy = strategy;
        this.appMode = appMode;
        this.pairs = pairs;
        this.numThreads = numThreads;
        this.parameters = parameters;
        this.resultWriteFile = resultWriteFile;
    }

    @Override
    public void run() {
        TimeSeries eurusd = DataProviderFactory.find(appMode).createDataProvider(Pair.eurUsd1()).readTimeSeries(fromTime.minusDays(1), toTime);

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, eurusd, fromTime, toTime, numThreads, Integer.MAX_VALUE);


        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategy,
                new ExcludeExistingParametersFilter(parameters),
                resultWriteFile,
                () -> {
                });
        backtestEngine.run();
    }
}
