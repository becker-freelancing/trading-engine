package com.becker.freelance.plugin;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;

import java.util.Set;

public class KrakenDemoAppMode implements AppMode {

    private final Set<Pair> allPairs;

    public KrakenDemoAppMode() {
        allPairs = Set.of(new KrakenPairProvider().get().toArray(new Pair[0]));
    }

    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "KRAKEN";
    }

    @Override
    public boolean isEqual(AppMode other) {
        return getDataSourceName().equals(other.getDataSourceName()) && isDemo() == other.isDemo();
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AppMode that = (AppMode) object;
        return isEqual(that);
    }
}
