package com.becker.freelance.strategies.strategy;


import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.indicators.ta.regime.DurationMarketRegime;
import com.becker.freelance.indicators.ta.regime.MarketRegime;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.indicators.ta.regime.RegimeIndicatorFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreator;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.Optional;

public abstract class BaseStrategy implements TradingStrategy {

    private final StrategyCreator strategyCreator;
    protected final BarSeries barSeries;
    protected final Indicator<Num> closePrice;
    private final Indicator<QuantileMarketRegime> regimeIndicator;
    private OpenPositionRequestor openPositionRequestor;
    private ZonedDateTime lastAddedBarTime;

    protected BaseStrategy(StrategyParameter strategyParameter) {
        this.strategyCreator = strategyParameter.strategyCreator();
        this.barSeries = new BaseBarSeries();
        this.closePrice = new ClosePriceIndicator(barSeries);

        Pair pair = strategyParameter.pair();
        RegimeIndicatorFactory regimeIndicatorFactory = new RegimeIndicatorFactory();
        Indicator<MarketRegime> marketRegimeIndicator = regimeIndicatorFactory.marketRegimeIndicatorFromConfigFile(pair.technicalName(), closePrice);
        Indicator<DurationMarketRegime> durationMarketRegimeIndicator = regimeIndicatorFactory.durationMarketRegimeIndicator(marketRegimeIndicator);
        this.regimeIndicator = regimeIndicatorFactory.quantileMarketRegimeIndicator(pair.technicalName(), durationMarketRegimeIndicator);
    }

    public Optional<EntrySignalBuilder> shouldEnter(EntryExecutionParameter entryParameter) {
        addBarIfNeeded(entryParameter.currentPriceAsBar());
        if (canNotExecute()) {
            return Optional.empty();
        }
        return internalShouldEnter(entryParameter);
    }

    public Optional<ExitSignal> shouldExit(ExitExecutionParameter exitParameter) {
        addBarIfNeeded(exitParameter.currentPriceAsBar());
        if (canNotExecute()) {
            return Optional.empty();
        }
        return internalShouldExit(exitParameter);
    }

    private boolean canNotExecute() {
        return barSeries.getBarCount() < regimeIndicator.getUnstableBars();
    }

    private void addBarIfNeeded(Bar currentPrice) {
        if (currentPrice.getEndTime().equals(lastAddedBarTime)) {
            return;
        }
        barSeries.addBar(currentPrice);
        lastAddedBarTime = currentPrice.getEndTime();
    }

    protected abstract Optional<EntrySignalBuilder> internalShouldEnter(EntryExecutionParameter entryParameter);

    protected abstract Optional<ExitSignal> internalShouldExit(ExitExecutionParameter exitParameter);

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

    protected OrderBuilder orderBuilder() {
        return OrderBuilder.getInstance();
    }

    protected EntrySignalBuilder entrySignalBuilder() {
        return EntrySignalBuilder.getInstance();
    }

    protected Decimal limitDistanceToLevel(TimeSeriesEntry currentPrice, Decimal distance, Direction direction) {
        return switch (direction) {
            case BUY -> currentPrice.getClosePriceForDirection(direction).add(distance);
            case SELL -> currentPrice.getClosePriceForDirection(direction).subtract(distance);
        };
    }

    protected Decimal stopDistanceToLevel(TimeSeriesEntry currentPrice, Decimal distance, Direction direction) {
        return switch (direction) {
            case BUY -> currentPrice.getClosePriceForDirection(direction).subtract(distance);
            case SELL -> currentPrice.getClosePriceForDirection(direction).add(distance);
        };
    }
}
