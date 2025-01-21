package com.becker.freelance.commons;

import java.time.LocalDateTime;

public class AppConfiguration {

    private final AppMode appMode;
    private final Integer numThreads;
    private final LocalDateTime startTime;

    public AppConfiguration(AppMode appMode, Integer numThreads, LocalDateTime startTime) {
        this.appMode = appMode;
        this.numThreads = numThreads;
        this.startTime = startTime;
    }

    public AppMode getAppMode() {
        return appMode;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
