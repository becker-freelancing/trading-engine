package com.becker.freelance.plugin;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;

import java.util.Set;

public class HistDataDemoAppMode implements AppMode {

    private final Set<Pair> allPairs;

    public HistDataDemoAppMode() {
        allPairs = Set.of(new HistDataPairProvider().get().toArray(new Pair[0]));
    }

    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "HISTDATA";
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
