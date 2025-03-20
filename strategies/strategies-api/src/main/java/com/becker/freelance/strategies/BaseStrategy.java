package com.becker.freelance.strategies;


import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.OpenPositionRequestor;

import java.time.LocalDateTime;
import java.util.*;

public abstract class BaseStrategy {


    public static List<BaseStrategy> loadAll(){
        ServiceLoader<BaseStrategy> strategies = ServiceLoader.load(BaseStrategy.class);
        return strategies.stream().map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparing(BaseStrategy::getName))
                .toList();
    }

    protected String name;
    protected PermutableStrategyParameter parameters;
    private boolean initiatedForParameter = false;
    private OpenPositionRequestor openPositionRequestor;

    public BaseStrategy(String name, PermutableStrategyParameter parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public BaseStrategy(Map<String, Decimal> parameters) {
        initiatedForParameter = true;
    }

    public abstract Optional<EntrySignal> shouldEnter(TimeSeries timeSeries, LocalDateTime time);

    public abstract Optional<ExitSignal> shouldExit(TimeSeries timeSeries, LocalDateTime time);

    public abstract BaseStrategy forParameters(Map<String, Decimal> parameters);

    public String getName() {
        return name;
    }

    public PermutableStrategyParameter getParameters() {
        return parameters;
    }

    public boolean isInitiatedForParameter() {
        return initiatedForParameter;
    }

    public OpenPositionRequestor getOpenPositionRequestor() {
        return openPositionRequestor;
    }

    public BaseStrategy withOpenPositionRequestor(OpenPositionRequestor openPositionRequestor) {
        this.openPositionRequestor = openPositionRequestor;
        return this;
    }
}
