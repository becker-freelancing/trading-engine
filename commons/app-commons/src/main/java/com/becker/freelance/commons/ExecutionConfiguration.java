package com.becker.freelance.commons;

import java.time.LocalDateTime;

public class ExecutionConfiguration {

    private final Pair pair;
    private final double initialWalletAmount;
    private final TimeSeries eurUsd;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public ExecutionConfiguration(Pair pair, double initialWalletAmount, TimeSeries eurUsd, LocalDateTime startTime, LocalDateTime endTime) {
        this.pair = pair;
        this.initialWalletAmount = initialWalletAmount;
        this.eurUsd = eurUsd;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public double getInitialWalletAmount() {
        return initialWalletAmount;
    }

    public Pair getPair() {
        return pair;
    }

    public TimeSeries getEurUsdTimeSeries() {
        return eurUsd;
    }

    public TimeSeries getEurUsd() {
        return eurUsd;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
