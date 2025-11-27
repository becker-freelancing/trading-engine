package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;

import java.util.Optional;

public interface TradingStrategy extends Initializable {

    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor);

    Optional<EntrySignalBuilder> shouldEnter(EntryExecutionParameter entryParameter);

    Optional<ExitSignal> shouldExit(ExitExecutionParameter exitParameter);

    public TradeableMarketRegime currentMarketRegime();

    public int unstableBars();

    public void beforeFirstBar(TradingStrategyInitiator beforeFirstBar);

    public Pair getPair();

    void reset();

    @Override
    default int initializationBarCount() {
        return unstableBars();
    }
}
