package com.becker.freelance.commons.app;


import com.becker.freelance.commons.service.ExtServiceLoader;

import java.util.List;

public interface AppMode {

    static List<AppMode> findAll() {
        return ExtServiceLoader.loadMultiple(AppMode.class).toList();
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

    default void onSelection() {
    }

    /**
     * Muss auch in equals Methode benutzt werden
     *
     * @param other
     * @return
     */
    boolean isEqual(AppMode other);

}
