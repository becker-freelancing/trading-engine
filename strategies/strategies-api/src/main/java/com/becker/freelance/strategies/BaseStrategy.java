package com.becker.freelance.strategies;


import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.executionparameter.EntryParameter;
import com.becker.freelance.strategies.executionparameter.ExitParameter;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.util.Optional;

public abstract class BaseStrategy implements TradingStrategy {

    protected final EntrySignalFactory entrySignalFactory;
    private final StrategyCreator strategyCreator;
    protected final BarSeries barSeries;
    protected final Indicator<Num> closePrice;
    private OpenPositionRequestor openPositionRequestor;

    protected BaseStrategy(StrategyCreator strategyCreator) {
        this.strategyCreator = strategyCreator;
        this.entrySignalFactory = new EntrySignalFactory();
        this.barSeries = new BaseBarSeries();
        this.closePrice = new ClosePriceIndicator(barSeries);
    }

    public Optional<EntrySignal> shouldEnter(EntryParameter entryParameter) {
        if (true) {
            throw new IllegalStateException("TODO");
        }
        Bar currentPrice = entryParameter.currentPriceAsBar();
        barSeries.addBar(currentPrice); //TODO: Nur Hinzufügen, falls in should Exit noch nicht hinzugefügt
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
