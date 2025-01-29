package com.becker.freelance.app;

import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class AbstractBacktestAppBuilder {

    public static AbstractBacktestAppBuilder builder(){
        return new AbstractBacktestAppBuilder();
    }

    private Decimal initialWalletAmount;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private boolean continueMode;

    AbstractBacktestAppBuilder(){}

    public AbstractBacktestAppBuilder withInitialWalletAmount(Decimal initialWalletAmount) {
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

    public AbstractBacktestAppBuilder continueMode(){
        this.continueMode = true;
        return this;
    }

    public Runnable build(){

        if (continueMode){
            return new AbstractBacktestContinueApp();
        }

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
