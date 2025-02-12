package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;

import java.util.function.Predicate;

public class AppModeMock implements AppMode {
    @Override
    public boolean isDemo() {
        return true;
    }

    @Override
    public String getDataSourceName() {
        return "MOCK";
    }

    @Override
    public Predicate<Pair> containingPairs() {
        return p -> true;
    }
}
