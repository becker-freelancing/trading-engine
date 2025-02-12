package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;

import java.util.function.Predicate;

public class AppModeMock2 implements AppMode {
    @Override
    public boolean isDemo() {
        return false;
    }

    @Override
    public String getDataSourceName() {
        return "MOCK2";
    }

    @Override
    public Predicate<Pair> containingPairs() {
        return p -> false;
    }
}
