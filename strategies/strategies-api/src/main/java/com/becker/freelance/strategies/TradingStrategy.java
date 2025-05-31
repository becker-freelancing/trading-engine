package com.becker.freelance.strategies;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.executionparameter.EntryParameter;
import com.becker.freelance.strategies.executionparameter.ExitParameter;

import java.util.Optional;

public interface TradingStrategy {

    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor);

    Optional<EntrySignal> shouldEnter(EntryParameter entryParameter);

    Optional<ExitSignal> shouldExit(ExitParameter exitParameter);

    public StrategyCreator strategyCreator();
}
