package com.becker.freelance.tradeexecution;

import com.becker.freelance.commons.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ServiceLoader;

public abstract class TradeExecutor {

    public static TradeExecutor find(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration){
        ServiceLoader<TradeExecutor> tradeExecutors = ServiceLoader.load(TradeExecutor.class);
        for (TradeExecutor tradeExecutor : tradeExecutors) {
            if (tradeExecutor.supports(appConfiguration.getAppMode())){
                return tradeExecutor.construct(executionConfiguration);
            }
        }
        throw new IllegalArgumentException("No TradeExecutor found for " + appConfiguration.getAppMode());
    }

    protected abstract TradeExecutor construct(ExecutionConfiguration executionConfiguration);

    protected abstract boolean supports(AppMode appMode);

    public abstract void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice);

    public abstract void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal);

    public abstract void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal);

    public abstract List<Trade> getAllClosedTrades();

    public abstract Wallet getWallet();
}
