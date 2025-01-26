package com.becker.freelance.strategies;


import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;
import java.util.*;

public abstract class BaseStrategy {


    public static List<BaseStrategy> loadAll(){
        ServiceLoader<BaseStrategy> strategies = ServiceLoader.load(BaseStrategy.class);
        return strategies.stream().map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparing(BaseStrategy::getName))
                .toList();
    }

    public static BaseStrategy loadByName(String name){
        List<BaseStrategy> all = loadAll();
        return all.stream().filter(str -> str.getName().equals(name)).findAny().orElseThrow(() -> new IllegalArgumentException("Could not find Strategy " + name + " in " + all));
    }

    protected String name;
    protected PermutableStrategyParameter parameters;
    private boolean initiatedForParameter = false;

    public BaseStrategy(String name, PermutableStrategyParameter parameters) {
        this.name = name;
        this.parameters = parameters;
    }


    public BaseStrategy(Map<String, Double> parameters) {
        initiatedForParameter = true;
    }

    public abstract Optional<EntrySignal> shouldEnter(TimeSeries timeSeries, LocalDateTime time);

    public abstract Optional<ExitSignal> shouldExit(TimeSeries timeSeries, LocalDateTime time);

    public abstract BaseStrategy forParameters(Map<String, Double> parameters);

    public int minNumberOfBarsRequired(Map<String, Double> parameters) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public PermutableStrategyParameter getParameters() {
        return parameters;
    }

    public boolean isInitiatedForParameter() {
        return initiatedForParameter;
    }
}
