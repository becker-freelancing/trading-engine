package com.becker.freelance.app;

import java.time.LocalDateTime;

public class AbstractBacktestAppBuilder {

    private Double initialWalletAmount;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;

    AbstractBacktestAppBuilder(){}

    public AbstractBacktestAppBuilder withInitialWalletAmount(Double initialWalletAmount) {
        this.initialWalletAmount = initialWalletAmount;
        return this;
    }

    public AbstractBacktestAppBuilder withFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
        return this;
    }

    public AbstractBacktestAppBuilder withToTime(LocalDateTime toTime) {
        this.toTime = toTime;
        return this;
    }

    public AbstractBacktestApp build(){
        if (initialWalletAmount == null){
            throw new IllegalStateException("InitialWalletAmount can not be null");
        }

        if (fromTime == null){
            throw new IllegalStateException("From time can not be null");
        }

        if (toTime == null){
            throw new IllegalStateException("To Time can not be null");
        }
        return new AbstractBacktestApp(initialWalletAmount, fromTime, toTime);
    }
}
