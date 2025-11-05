package com.becker.freelance.app;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.creation.RegimeStrategyCreator;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.trading.abstractapp.commons.strategyconfig.StrategyFileConfigurator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RemoteBacktestAppInitiatingUtil {


    public List<RegimeStrategyCreator> askStrategy(Pair pair) {
        String strategies = System.getenv("STRATEGIES");
        Set<String> strategieNames = Arrays.stream(strategies.split(";")).map(String::trim).collect(Collectors.toSet());
        StrategyFileConfigurator fileConfigurator = new StrategyFileConfigurator();
        return StrategyCreator.findAll().stream()
                .filter(strategy -> strategieNames.contains(strategy.strategyName()))
                .flatMap(strategyCreator -> fileConfigurator.withConfigFile(strategyCreator, pair))
                .toList();
    }


    public List<Pair> askPair(AppMode appMode) {
        String pairs = System.getenv("PAIRS");
        Set<String> pairNames = Arrays.stream(pairs.split(";")).map(String::trim).collect(Collectors.toSet());
        return Pair.allPairs().stream()
                .filter(pair -> pairNames.contains(pair.shortName()))
                .filter(pair -> pair.isExecutableInAppMode(appMode)).toList();
    }

    public AppMode askAppMode() {
        String appmode = System.getenv("APPMODE");
        return AppMode.findAll().stream().filter(mode -> mode.getDescription().equals(appmode)).findAny().orElseThrow(() -> new IllegalStateException("Could not find valid App Mode"));
    }

}
