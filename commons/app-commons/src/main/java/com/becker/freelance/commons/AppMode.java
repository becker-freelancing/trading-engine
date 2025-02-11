package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public interface AppMode {

    static List<AppMode> findAll() {
        ServiceLoader<AppMode> appModes = ServiceLoader.load(AppMode.class);
        return appModes.stream().map(ServiceLoader.Provider::get).toList();
    }

    static AppMode fromDescription(String description) {
        return findAll().stream()
                .filter(mode -> description.equals(mode.getDescription()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Could not find App Mode with description " + description));
    }

    boolean isDemo();

    String getDataSourceName();

    default String getDescription() {
        return getDataSourceName() + "_" + (isDemo() ? "DEMO" : "LIVE");
    }

    Predicate<Pair> containingPairs();
}
