package com.becker.freelance.strategies.strategy;


import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.indicators.ta.regime.RegimeIndicatorFactory;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;
import com.becker.freelance.trading.external.services.registry.ExternalServiceRegistry;
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

public abstract class SingleTimeFrameBaseStrategy implements TradingStrategy {

    private static final Logger logger = LoggerFactory.getLogger(SingleTimeFrameBaseStrategy.class);
    protected final BarSeries barSeries;
    protected final Indicator<Num> closePrice;
    private final Pair pair;
    private final Indicator<TradeableMarketRegime> regimeIndicator;
    private final Set<BiConsumer<TradingStrategy, LocalDateTime>> beforeFirstBar;
    private final ExternalServiceRegistry externalServiceRegistry;
    private OpenPositionRequestor openPositionRequestor;
    private ZonedDateTime lastAddedBarTime;
    private boolean initiated = false;

    protected SingleTimeFrameBaseStrategy(StrategyParameter strategyParameter) {
        this.barSeries = new BaseBarSeries();
        this.closePrice = new ClosePriceIndicator(barSeries);
        this.externalServiceRegistry = new ExternalServiceRegistry();

        Pair pair = strategyParameter.pair();
        RegimeIndicatorFactory regimeIndicatorFactory = new RegimeIndicatorFactory();
        this.regimeIndicator = regimeIndicatorFactory.marketRegimeIndicatorForStrategy(pair, closePrice);
        this.beforeFirstBar = new HashSet<>();
        this.pair = strategyParameter.pair();
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
