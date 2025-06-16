package com.becker.freelance.app;

import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.engine.StrategySupplier;
import com.becker.freelance.execution.RemoteExecutionEngine;
import com.becker.freelance.execution.StrategyWithPair;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.strategies.strategy.BaseStrategy;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import com.becker.freelance.strategies.strategy.RegimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AbstractRemoteBacktestApp implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(AbstractRemoteBacktestApp.class);

    private final RemoteBacktestAppInitiatingUtil appInitiatingUtil;

    AbstractRemoteBacktestApp() {
        this.appInitiatingUtil = new RemoteBacktestAppInitiatingUtil();
    }

    @Override
    public void run() {
        AppMode appMode = appInitiatingUtil.askAppMode();
        logger.info("AppMode: {}", appMode.getDescription());

        List<Pair> pairs = appInitiatingUtil.askPair(appMode);
        logger.info("Pairs: {}", pairs.stream().map(Pair::technicalName).toList());

        AppConfiguration appConfiguration = new AppConfiguration(appMode, LocalDateTime.now());
        List<RegimeStrategyCreator> regimeStrategyCreators = pairs.stream()
                .map(appInitiatingUtil::askStrategy)
                .flatMap(List::stream)
                .peek(creator -> logger.info("Using Strategy: {}", creator))
                .toList();

        List<StrategyWithPair> grouped = groupByPair(regimeStrategyCreators);

        RemoteExecutionEngine remoteExecutionEngine = new RemoteExecutionEngine(grouped, appConfiguration);
        remoteExecutionEngine.run();
    }

    private List<StrategyWithPair> groupByPair(List<RegimeStrategyCreator> regimeStrategyCreators) {
        Map<Pair, List<RegimeStrategyCreator>> grouped = new HashMap<>();
        for (RegimeStrategyCreator regimeStrategyCreator : regimeStrategyCreators) {
            grouped.computeIfAbsent(regimeStrategyCreator.pair(), k -> new ArrayList<>());
            grouped.get(regimeStrategyCreator.pair()).add(regimeStrategyCreator);
        }

        return grouped.entrySet().stream().map(entry -> toRegimeStrategy(entry.getKey(), entry.getValue())).toList();

    }

    private StrategyWithPair toRegimeStrategy(Pair pair, List<RegimeStrategyCreator> strategyCreators) {
        StrategySupplier regimeStrategySupplier = toRegimeStrategySupplier(strategyCreators);
        return new StrategyWithPair(regimeStrategySupplier, pair);
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

    private StrategySupplier toSupplier(RegimeStrategyCreator strategy) {
        return (pair, tradingCalculator) -> {
            if (!strategy.pair().equals(pair)) {
                throw new IllegalStateException("Could not construct Strategy for pair: " + pair.technicalName() + ". Expected Pair was " + strategy.pair().technicalName());
            }
            DefaultStrategyParameter defaultStrategyParameter = new DefaultStrategyParameter(
                    strategy.strategyCreationParameter(),
                    pair,
                    strategy.regimes());
            return strategy.build(defaultStrategyParameter);
        };
    }
}
