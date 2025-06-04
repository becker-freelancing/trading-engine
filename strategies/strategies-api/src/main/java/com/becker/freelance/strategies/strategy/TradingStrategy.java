package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;

import java.util.Optional;

public interface TradingStrategy {

    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor);

    Optional<EntrySignal> shouldEnter(EntryExecutionParameter entryParameter);

    Optional<ExitSignal> shouldExit(ExitExecutionParameter exitParameter);

    public StrategyCreator strategyCreator();

    public QuantileMarketRegime currentMarketRegime();
}
