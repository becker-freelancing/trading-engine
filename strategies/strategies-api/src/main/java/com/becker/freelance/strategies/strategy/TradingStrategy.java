package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface TradingStrategy {

    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor);

    Optional<EntrySignalBuilder> shouldEnter(EntryExecutionParameter entryParameter);

    Optional<ExitSignal> shouldExit(ExitExecutionParameter exitParameter);

    public QuantileMarketRegime currentMarketRegime();

    public int unstableBars();

    public void processInitData(TimeSeries initiationData);

    public void beforeFirstBar(BiConsumer<TradingStrategy, LocalDateTime> beforeFirstBar);

    public Pair getPair();
}
