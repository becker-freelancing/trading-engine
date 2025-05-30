package com.becker.freelance.strategies;


import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.OpenPositionRequestor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseStrategy {


    public static List<BaseStrategy> loadAll(){
        return ExtServiceLoader.loadMultiple(BaseStrategy.class)
                .sorted(Comparator.comparing(BaseStrategy::getName))
                .toList();
    }

    protected String name;
    protected PermutableStrategyParameter parameters;
    private boolean initiatedForParameter = false;
    private OpenPositionRequestor openPositionRequestor;
    protected EntrySignalFactory entrySignalFactory;

    public BaseStrategy(String name, PermutableStrategyParameter parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public BaseStrategy(Map<String, Decimal> parameters) {
        initiatedForParameter = true;
        entrySignalFactory = new EntrySignalFactory();
    }

    public abstract Optional<EntrySignal> shouldEnter(EntryParameter entryParameter);

    public abstract Optional<ExitSignal> shouldExit(ExitParameter exitParameter);

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
