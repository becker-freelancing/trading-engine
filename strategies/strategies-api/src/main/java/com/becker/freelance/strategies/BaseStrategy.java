package com.becker.freelance.strategies;


import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.indicators.ta.regime.DurationMarketRegime;
import com.becker.freelance.indicators.ta.regime.MarketRegime;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.indicators.ta.regime.RegimeIndicatorFactory;
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

import java.time.ZonedDateTime;
import java.util.Optional;

public abstract class BaseStrategy implements TradingStrategy {

    protected final EntrySignalFactory entrySignalFactory;
    private final StrategyCreator strategyCreator;
    protected final BarSeries barSeries;
    protected final Indicator<Num> closePrice;
    private final Indicator<QuantileMarketRegime> regimeIndicator;
    private OpenPositionRequestor openPositionRequestor;
    private ZonedDateTime lastAddedBarTime;

    protected BaseStrategy(StrategyCreator strategyCreator, Pair pair) {
        this.strategyCreator = strategyCreator;
        this.entrySignalFactory = new EntrySignalFactory();
        this.barSeries = new BaseBarSeries();
        this.closePrice = new ClosePriceIndicator(barSeries);

        RegimeIndicatorFactory regimeIndicatorFactory = new RegimeIndicatorFactory();
        Indicator<MarketRegime> marketRegimeIndicator = regimeIndicatorFactory.marketRegimeIndicatorFromConfigFile(pair.technicalName(), closePrice);
        Indicator<DurationMarketRegime> durationMarketRegimeIndicator = regimeIndicatorFactory.durationMarketRegimeIndicator(marketRegimeIndicator);
        this.regimeIndicator = regimeIndicatorFactory.quantileMarketRegimeIndicator(pair.technicalName(), durationMarketRegimeIndicator);
    }

    public Optional<EntrySignal> shouldEnter(EntryParameter entryParameter) {
        addBarIfNeeded(entryParameter.currentPriceAsBar());
        return internalShouldEnter(entryParameter);
    }

    public Optional<ExitSignal> shouldExit(ExitParameter exitParameter) {
        addBarIfNeeded(exitParameter.currentPriceAsBar());
        return internalShouldExit(exitParameter);
    }

    private void addBarIfNeeded(Bar currentPrice) {
        if (currentPrice.getEndTime().equals(lastAddedBarTime)) {
            return;
        }
        barSeries.addBar(currentPrice);
        lastAddedBarTime = currentPrice.getEndTime();
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

    @Override
    public QuantileMarketRegime currentMarketRegime() {
        return regimeIndicator.getValue(barSeries.getEndIndex());
    }
}
