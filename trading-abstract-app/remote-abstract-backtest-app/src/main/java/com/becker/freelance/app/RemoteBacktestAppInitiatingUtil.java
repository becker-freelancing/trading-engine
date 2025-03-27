package com.becker.freelance.app;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.strategies.BaseStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class RemoteBacktestAppInitiatingUtil {


    public List<BaseStrategy> askStrategy() {
        String strategies = System.getenv("STRATEGIES");
        Set<String> strategieNames = Arrays.stream(strategies.split(";")).map(String::trim).collect(Collectors.toSet());
        return BaseStrategy.loadAll().stream().filter(strategy -> strategieNames.contains(strategy.getName())).toList();
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
