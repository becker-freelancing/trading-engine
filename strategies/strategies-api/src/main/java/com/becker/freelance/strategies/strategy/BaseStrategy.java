package com.becker.freelance.strategies.strategy;


import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.indicators.ta.regime.DurationMarketRegime;
import com.becker.freelance.indicators.ta.regime.MarketRegime;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.indicators.ta.regime.RegimeIndicatorFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseStrategy implements TradingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BaseStrategy.class);

    private final Pair pair;
    protected final BarSeries barSeries;
    protected final Indicator<Num> closePrice;
    private final Indicator<QuantileMarketRegime> regimeIndicator;
    private final Set<BiConsumer<TradingStrategy, LocalDateTime>> beforeFirstBar;
    private final Set<Consumer<Bar>> onBarAdded;
    private OpenPositionRequestor openPositionRequestor;
    private ZonedDateTime lastAddedBarTime;
    private boolean initiated = false;

    protected BaseStrategy(StrategyParameter strategyParameter) {
        this.barSeries = new BaseBarSeries();
        this.closePrice = new ClosePriceIndicator(barSeries);

        Pair pair = strategyParameter.pair();
        RegimeIndicatorFactory regimeIndicatorFactory = new RegimeIndicatorFactory();
        Indicator<MarketRegime> marketRegimeIndicator = regimeIndicatorFactory.marketRegimeIndicatorFromConfigFile(pair.technicalName(), closePrice);
        Indicator<DurationMarketRegime> durationMarketRegimeIndicator = regimeIndicatorFactory.durationMarketRegimeIndicator(marketRegimeIndicator);
        this.regimeIndicator = regimeIndicatorFactory.quantileMarketRegimeIndicator(pair.technicalName(), durationMarketRegimeIndicator);
        this.beforeFirstBar = new HashSet<>();
        this.pair = strategyParameter.pair();
        this.onBarAdded = new HashSet<>();
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
        return barSeries.getEndIndex() < unstableBars();
    }

    protected void addBarIfNeeded(Bar currentPrice) {
        if (currentPrice.getEndTime().equals(lastAddedBarTime)) {
            return;
        }
        if (!initiated && barSeries.isEmpty()) {
            logger.info("Initiating trading strategy");
            initiated = true;
            beforeFirstBar.forEach(initiator -> initiator.accept(this, currentPrice.getEndTime().toLocalDateTime()));
        }
        barSeries.addBar(currentPrice);
        lastAddedBarTime = currentPrice.getEndTime();
        onBarAdded.forEach(consumer -> consumer.accept(currentPrice));
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
    public QuantileMarketRegime currentMarketRegime() {
        return regimeIndicator.getValue(barSeries.getEndIndex());
    }

    @Override
    public int unstableBars() {
        return regimeIndicator.getUnstableBars();
    }

    @Override
    public void beforeFirstBar(BiConsumer<TradingStrategy, LocalDateTime> beforeFirstBar) {
        this.beforeFirstBar.add(beforeFirstBar);
    }

    @Override
    public Pair getPair() {
        return pair;
    }

    @Override
    public void processInitData(TimeSeries initiationData) {
        LocalDateTime minTime = initiationData.getMinTime();
        LocalDateTime maxTime = initiationData.getMaxTime();
        Duration duration = initiationData.getPair().toDuration();

        while (!minTime.isAfter(maxTime)) {

            Bar bar = initiationData.getEntryForTimeAsBar(minTime);
            addBarIfNeeded(bar);

            minTime = minTime.plus(duration);
        }
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
