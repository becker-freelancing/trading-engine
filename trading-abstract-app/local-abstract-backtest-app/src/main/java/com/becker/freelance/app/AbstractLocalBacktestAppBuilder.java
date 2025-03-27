package com.becker.freelance.app;

import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class AbstractLocalBacktestAppBuilder {

    AbstractLocalBacktestAppBuilder() {
    }

    private Decimal initialWalletAmount;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private boolean continueMode;

    public static AbstractLocalBacktestAppBuilder builder() {
        return new AbstractLocalBacktestAppBuilder();
    }

    public AbstractLocalBacktestAppBuilder withInitialWalletAmount(Decimal initialWalletAmount) {
        this.initialWalletAmount = initialWalletAmount;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
        return this;
    }

    public AbstractLocalBacktestAppBuilder withToTime(LocalDateTime toTime) {
        this.toTime = toTime;
        return this;
    }

    public AbstractLocalBacktestAppBuilder continueMode() {
        this.continueMode = true;
        return this;
    }

    public Runnable build(){

        if (continueMode){
            return new AbstractLocalBacktestContinueApp();
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
        return new AbstractLocalBacktestApp(initialWalletAmount, fromTime, toTime);
    }
}
