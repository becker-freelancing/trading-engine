package com.becker.freelance.plugin;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;

import java.util.Set;
import java.util.function.Predicate;

public class BinanceDemoAppMode implements AppMode {


    private final Set<Pair> allPairs;

    public BinanceDemoAppMode() {
        allPairs = Set.of(new BinancePairProvider().get().toArray(new Pair[0]));
    }

    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "BINANCE";
    }

    @Override
    public Predicate<Pair> containingPairs() {
        return allPairs::contains;
    }
}
