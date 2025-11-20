package com.becker.freelance.strategies.strategy;


import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.indicators.ta.other.ClosePriceTemporalIndicator;
import com.becker.freelance.indicators.ta.other.HighPriceTemporalIndicator;
import com.becker.freelance.indicators.ta.other.LowPriceTemporalIndicator;
import com.becker.freelance.indicators.ta.regime.RegimeIndicatorFactory;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeriesImpl;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;
import com.becker.freelance.trading.external.services.registry.ScopedExternalServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;

public abstract class SingleTimeFrameBaseStrategy implements TradingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SingleTimeFrameBaseStrategy.class);
    protected final TemporalBarSeries barSeries;
    protected final TemporalIndicator<Decimal> closePrice;
    protected final TemporalIndicator<Decimal> lowPrice;
    protected final TemporalIndicator<Decimal> highPrice;
    private final Pair pair;
    private final TemporalIndicator<TradeableMarketRegime> regimeIndicator;
    private final Set<BiConsumer<TradingStrategy, LocalDateTime>> beforeFirstBar;
    private OpenPositionRequestor openPositionRequestor;
    private LocalDateTime lastAddedBarTime;
    private boolean initiated = false;
    private final ScopedExternalServiceRegistry scopedExternalServiceRegistry;
    private LocalDateTime currentTime;
    private int unstableBars = -1;

    protected SingleTimeFrameBaseStrategy(StrategyParameter strategyParameter) {
        this.barSeries = new TemporalBarSeriesImpl(strategyParameter.pair());
        this.closePrice = new ClosePriceTemporalIndicator(barSeries);
        this.lowPrice = new LowPriceTemporalIndicator(barSeries);
        this.highPrice = new HighPriceTemporalIndicator(barSeries);

        Pair pair = strategyParameter.pair();
        RegimeIndicatorFactory regimeIndicatorFactory = new RegimeIndicatorFactory();
        this.regimeIndicator = regimeIndicatorFactory.marketRegimeIndicatorForStrategy(pair, closePrice);
        this.beforeFirstBar = new HashSet<>();
        this.pair = strategyParameter.pair();
        this.scopedExternalServiceRegistry = strategyParameter.scopedExternalServiceRegistry();
    }

    public Optional<EntrySignalBuilder> shouldEnter(EntryExecutionParameter entryParameter) {
        this.currentTime = entryParameter.time();
        addBarIfNeeded(entryParameter.currentPrice());
        if (canNotExecute()) {
            return Optional.empty();
        }
        return internalShouldEnter(entryParameter);
    }

    public Optional<ExitSignal> shouldExit(ExitExecutionParameter exitParameter) {
        this.currentTime = exitParameter.time();
        addBarIfNeeded(exitParameter.currentPrice());
        if (canNotExecute()) {
            return Optional.empty();
        }
        return internalShouldExit(exitParameter);
    }

    private boolean canNotExecute() {
        if (unstableBars == -1) {
            unstableBars = unstableBars();
        }
        return barSeries.getSize() < unstableBars;
    }

    protected void addBarIfNeeded(TimeSeriesEntry currentPrice) {
        if (currentPrice.time().equals(lastAddedBarTime)) {
            return;
        }
        if (!initiated && barSeries.isEmpty()) {
            logger.info("Initiating trading strategy at time {}...", currentPrice.time());
            initiated = true;
            beforeFirstBar.forEach(initiator -> initiator.accept(this, currentPrice.time()));
            logger.info("Finished initiating trading strategy at time {}", currentPrice.time());
        }
        barSeries.addBar(currentPrice);
        lastAddedBarTime = currentPrice.time();
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
    public TradeableMarketRegime currentMarketRegime() {
        return regimeIndicator.getValue(currentTime);
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

            TimeSeriesEntry bar = initiationData.getEntryForTime(minTime);
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

    protected ScopedExternalServiceRegistry getScopedExternalServiceRegistry() {
        return scopedExternalServiceRegistry;
    }

    protected LocalDateTime currentTime() {
        return currentTime;
    }

    protected LocalDateTime lastTime() {
        return timeBeforeNCandles(1);
    }

    protected LocalDateTime timeBeforeNCandles(int n) {
        return currentTime.minus(pair.toDuration().multipliedBy(n));
    }

    protected List<LocalDateTime> timesBetweenIncludingBoth(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> times = new ArrayList<>();
        while (start.isBefore(end) || start.isEqual(end)) {
            times.add(start);
            start = start.plus(pair.toDuration());
        }
        return times;
    }

    protected Duration pairDuration() {
        return pair.toDuration();
    }

    protected abstract void resetIndicators();

    @Override
    public final void reset() {
        resetIndicators();
        initiated = false;
    }
}
