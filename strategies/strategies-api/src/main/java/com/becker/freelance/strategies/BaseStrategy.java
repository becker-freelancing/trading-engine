package com.becker.freelance.strategies;


import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.executionparameter.EntryParameter;
import com.becker.freelance.strategies.executionparameter.ExitParameter;

import java.util.Optional;

public abstract class BaseStrategy implements TradingStrategy {

    protected final EntrySignalFactory entrySignalFactory;
    private final StrategyCreator strategyCreator;
    private OpenPositionRequestor openPositionRequestor;

    protected BaseStrategy(StrategyCreator strategyCreator) {
        this.strategyCreator = strategyCreator;
        this.entrySignalFactory = new EntrySignalFactory();
    }

    public Optional<EntrySignal> shouldEnter(EntryParameter entryParameter) {
        return internalShouldEnter(entryParameter);
    }

    public Optional<ExitSignal> shouldExit(ExitParameter exitParameter) {
        return internalShouldExit(exitParameter);
    }

    protected abstract Optional<EntrySignal> internalShouldEnter(EntryParameter entryParameter);

    protected abstract Optional<ExitSignal> internalShouldExit(ExitParameter exitParameter);

    public OpenPositionRequestor getOpenPositionRequestor() {
        return openPositionRequestor;
    }

    @Override
    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor) {
        this.openPositionRequestor = openPositionRequestor;
    }

    @Override
    public StrategyCreator strategyCreator() {
        return strategyCreator;
    }
}
