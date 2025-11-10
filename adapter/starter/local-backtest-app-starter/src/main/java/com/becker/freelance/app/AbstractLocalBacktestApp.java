package com.becker.freelance.app;

import com.becker.freelance.backtest.StrategySupplierWithParameters;
import com.becker.freelance.backtest.configuration.BacktestMode;
import com.becker.freelance.backtest.configuration.BacktestStage;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.indicators.ta.regime.TradeableMarketRegimeWrapper;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.RegimeStrategyCreator;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.strategy.RegimeStrategy;
import com.becker.freelance.strategies.strategy.SingleTimeFrameBaseStrategy;
import com.becker.freelance.trading.abstractapp.commons.strategyconfig.StrategyFileConfigurator;
import com.becker.freelance.trading.api.LocalBacktestPort;
import com.becker.freelance.trading.application.LocalBacktestInteractor;
import com.becker.freelance.trading.application.LocalBacktestWithStrategyConfigInteractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class AbstractLocalBacktestApp {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLocalBacktestApp.class);


    private final Decimal initialWalletAmount;
    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final Runnable onFinished;
    private final boolean useStrategyConfig;
    private final BacktestMode backtestMode;
    private final BacktestStage backtestStage;

    AbstractLocalBacktestApp(Decimal initialWalletAmount, LocalDateTime fromTime, LocalDateTime toTime, Runnable onFinished, boolean strategyConfig, BacktestMode backtestMode, BacktestStage backtestStage) {
        this.initialWalletAmount = initialWalletAmount;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.onFinished = onFinished;
        this.useStrategyConfig = strategyConfig;
        this.backtestMode = backtestMode;
        this.backtestStage = backtestStage;
    }

    protected abstract void initiate();

    protected abstract StrategyCreator getStrategyCreator();

    protected abstract AppMode getAppMode();

    protected abstract List<Pair> getPairs();

    protected abstract Integer getNumThreads();

    protected abstract Integer getParameterLimit();

    public LocalBacktestPort build() {
        initiate();
        StrategyCreator strategy = getStrategyCreator();

        if (useStrategyConfig) {
            return runWithStrategyConfig(strategy, onFinished);
        } else {
            return runWithoutStrategyConfig(strategy, onFinished);
        }
    }

    private LocalBacktestPort runWithoutStrategyConfig(StrategyCreator strategy, Runnable onFinished) {
        return new LocalBacktestInteractor(
                initialWalletAmount,
                fromTime,
                toTime,
                onFinished,
                strategy,
                getAppMode(),
                getPairs(),
                getNumThreads(),
                getParameterLimit(),
                backtestMode,
                backtestStage
        );
    }

    private LocalBacktestPort runWithStrategyConfig(StrategyCreator strategy, Runnable onFinished) {
        logger.warn("USING STRATEGY CONFIG!!!");
        StrategyFileConfigurator fileConfigurator = new StrategyFileConfigurator();
        List<RegimeStrategyCreator> strategyCreators = getPairs().stream()
                .flatMap(pair -> fileConfigurator.withConfigFile(strategy, pair))
                .toList();

        List<StrategySupplierWithParameters> strategySuppliers = groupByPair(strategyCreators).map(this::withDummyParameters).toList();

        return new LocalBacktestWithStrategyConfigInteractor(
                initialWalletAmount,
                fromTime,
                toTime,
                onFinished,
                strategy,
                getAppMode(),
                getPairs(),
                getNumThreads(),
                getParameterLimit(),
                strategySuppliers,
                backtestMode,
                backtestStage
        );
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
        return (pair, tradingCalculator, scopedExternalServiceRegistry) -> {
            Map<TradeableMarketRegime, List<SingleTimeFrameBaseStrategy>> strategiesByRegime = TradeableMarketRegimeWrapper.all().stream().collect(Collectors.toMap(
                    regime -> regime,
                    regime -> strategyCreators.stream().filter(strategyCreator -> strategyCreator.regimes().contains(regime))
                            .sorted(Comparator.comparing(RegimeStrategyCreator::priority))
                            .map(strategyCreator -> (SingleTimeFrameBaseStrategy) strategyCreator.build(strategyCreator.strategyParameterForRegime(regime, scopedExternalServiceRegistry)))
                            .toList()
            ));

            return new RegimeStrategy(pair, strategiesByRegime);
        };
    }


}
