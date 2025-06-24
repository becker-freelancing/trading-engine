package com.becker.freelance.app;

import com.becker.freelance.app.BacktestAppInitiatingUtil.LastExecutionProperties;
import com.becker.freelance.backtest.BacktestEngine;
import com.becker.freelance.backtest.StrategySupplierWithParameters;
import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProviderFactory;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.RegimeStrategyCreator;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.BaseStrategy;
import com.becker.freelance.strategies.strategy.RegimeStrategy;
import com.becker.freelance.trading.abstractapp.commons.strategyconfig.StrategyFileConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AbstractLocalBacktestApp implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLocalBacktestApp.class);


    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final BacktestAppInitiatingUtil appInitiatingUtil;
    private final Runnable onFinished;
    private final boolean useStrategyConfig;

    AbstractLocalBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, Runnable onFinished, boolean strategyConfig) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.appInitiatingUtil = new BacktestAppInitiatingUtil();
        this.onFinished = onFinished;
        this.useStrategyConfig = strategyConfig;
    }

    @Override
    public void run() {
        Optional<LastExecutionProperties> properties = appInitiatingUtil.findProperties();
        StrategyCreator strategy;
        AppMode appMode;
        List<Pair> pairs;
        Integer numThreads;
        if (properties.isPresent()) {
            LastExecutionProperties lastExecutionProperties = properties.get();
            strategy = lastExecutionProperties.baseStrategy();
            appMode = lastExecutionProperties.appMode();
            pairs = lastExecutionProperties.pairs();
            numThreads = lastExecutionProperties.numberOfThread();
        } else {
            strategy = appInitiatingUtil.askStrategy();
            appMode = appInitiatingUtil.askAppMode();
            pairs = appInitiatingUtil.askPair(appMode);
            numThreads = appInitiatingUtil.askNumberOfThreads();
        }
        appInitiatingUtil.saveProperties(strategy, numThreads, pairs, appMode);


        TimeSeries eurusd = DataProviderFactory.find(appMode).createDataProvider(Pair.eurUsd1()).readTimeSeries(fromTime.minusDays(1), toTime);

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        BacktestExecutionConfiguration backtestExecutionConfiguration = new BacktestExecutionConfiguration(pairs, initialWalletAmount, eurusd, fromTime, toTime, numThreads);


        if (useStrategyConfig) {
            runWithStrategyConfig(appConfiguration, backtestExecutionConfiguration, strategy, onFinished);
        } else {
            runWithoutStrategyConfig(appConfiguration, backtestExecutionConfiguration, strategy, onFinished);
        }
    }

    private void runWithoutStrategyConfig(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategy, Runnable onFinished) {
        logger.info("\t\tAnzahl Permutationen {}", strategy.strategyParameters().permutate().size());

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategy, onFinished);
        backtestEngine.run();
    }

    private void runWithStrategyConfig(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, StrategyCreator strategy, Runnable onFinished) {
        logger.warn("USING STRATEGY CONFIG!!!");
        StrategyFileConfigurator fileConfigurator = new StrategyFileConfigurator();
        List<RegimeStrategyCreator> strategyCreators = backtestExecutionConfiguration.pairs().stream()
                .flatMap(pair -> fileConfigurator.withConfigFile(strategy, pair))
                .toList();

        List<StrategySupplierWithParameters> strategySuppliers = groupByPair(strategyCreators).map(this::withDummyParameters).toList();

        logger.info("\t\tAnzahl Permutationen {}", strategySuppliers.size());

        BacktestEngine backtestEngine = new BacktestEngine(appConfiguration, backtestExecutionConfiguration, strategySuppliers, onFinished, strategy.strategyName());
        backtestEngine.run();
    }

    private StrategySupplierWithParameters withDummyParameters(StrategySupplier strategySupplier) {
        return new StrategySupplierWithParameters(
                strategySupplier,
                new DummyStrategyCreationParameter()
        );
    }


    private Stream<StrategySupplier> groupByPair(List<RegimeStrategyCreator> regimeStrategyCreators) {
        Map<Pair, List<RegimeStrategyCreator>> grouped = new HashMap<>();
        for (RegimeStrategyCreator regimeStrategyCreator : regimeStrategyCreators) {
            grouped.computeIfAbsent(regimeStrategyCreator.pair(), k -> new ArrayList<>());
            grouped.get(regimeStrategyCreator.pair()).add(regimeStrategyCreator);
        }

        return grouped.values().stream().map(this::toRegimeStrategySupplier);

    }

    private StrategySupplier toRegimeStrategySupplier(List<RegimeStrategyCreator> strategyCreators) {
        return (pair, tradingCalculator) -> {
            Map<QuantileMarketRegime, List<BaseStrategy>> strategiesByRegime = QuantileMarketRegime.all().stream().collect(Collectors.toMap(
                    regime -> regime,
                    regime -> strategyCreators.stream().filter(strategyCreator -> strategyCreator.regimes().contains(regime))
                            .sorted(Comparator.comparing(RegimeStrategyCreator::priority))
                            .map(strategyCreator -> (BaseStrategy) strategyCreator.build(strategyCreator.strategyParameterForRegime(regime)))
                            .toList()
            ));

            return new RegimeStrategy(pair, strategiesByRegime);
        };
    }


}
