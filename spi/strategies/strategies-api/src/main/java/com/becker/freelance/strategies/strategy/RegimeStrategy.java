package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;
import com.becker.freelance.trading.external.services.registry.ScopedExternalServiceRegistry;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RegimeStrategy extends SingleTimeFrameBaseStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RegimeStrategy.class);

    private final Map<TradeableMarketRegime, List<SingleTimeFrameBaseStrategy>> strategiesByRegime;
    private final List<SingleTimeFrameBaseStrategy> allStrategies;

    public RegimeStrategy(Pair pair, Map<TradeableMarketRegime, List<SingleTimeFrameBaseStrategy>> strategiesByRegime) {
        super(new PairStrategyParameter(pair));
        this.strategiesByRegime = strategiesByRegime;
        this.allStrategies = strategiesByRegime.values().stream().flatMap(Collection::stream).toList();
    }


    @Override
    protected Optional<EntrySignalBuilder> internalShouldEnter(EntryExecutionParameter entryParameter) {
        TradeableMarketRegime currentMarketRegime = currentMarketRegime();

        logger.debug("Current market regime is {}", currentMarketRegime.name());

        for (SingleTimeFrameBaseStrategy baseStrategy : strategiesByRegime.getOrDefault(currentMarketRegime, new ArrayList<>())) {
            logger.debug("Asking Strategy {} for entry signal", baseStrategy);
            Optional<EntrySignalBuilder> entrySignalBuilder = baseStrategy.internalShouldEnter(entryParameter);
            if (entrySignalBuilder.isPresent()) {
                logger.debug("Got entry signal from strategy {}", baseStrategy);
                return entrySignalBuilder;
            }
        }
        logger.debug("Did not got entry signal from strategies {}", strategiesByRegime.get(currentMarketRegime));
        return Optional.empty();
    }

    @Override
    protected Optional<ExitSignal> internalShouldExit(ExitExecutionParameter exitParameter) {
        TradeableMarketRegime currentMarketRegime = currentMarketRegime();
        for (SingleTimeFrameBaseStrategy baseStrategy : strategiesByRegime.getOrDefault(currentMarketRegime, new ArrayList<>())) {
            Optional<ExitSignal> exitSignal = baseStrategy.internalShouldExit(exitParameter);
            if (exitSignal.isPresent()) {
                return exitSignal;
            }
        }
        return Optional.empty();
    }

    @Override
    public int unstableBars() {
        return Math.max(super.unstableBars(),
                strategiesByRegime.values().stream()
                        .flatMap(List::stream)
                        .findAny()
                        .map(SingleTimeFrameBaseStrategy::unstableBars)
                        .orElse(0)
        );
    }

    @Override
    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor) {
        super.setOpenPositionRequestor(openPositionRequestor);
        allStrategies.forEach(strategy -> strategy.setOpenPositionRequestor(openPositionRequestor));
    }

    @Override
    protected List<TemporalIndicator<?>> getIndicators() {
        return allStrategies.stream()
                .map(SingleTimeFrameBaseStrategy::getIndicators)
                .flatMap(List::stream)
                .toList();
    }

    @Override
    protected void addBarIfNeeded(TimeSeriesEntry currentPrice) {
        super.addBarIfNeeded(currentPrice);
        allStrategies.forEach(strategy -> strategy.addBarIfNeeded(currentPrice));
    }

    @Override
    protected void resetIndicators() {
        allStrategies.forEach(SingleTimeFrameBaseStrategy::resetIndicators);
    }

    @Override
    public List<Initializable> getTransitiveInitializables() {
        return allStrategies.stream()
                .map(TradingStrategy::getTransitiveInitializables)
                .flatMap(List::stream)
                .toList();
    }

    private static final record PairStrategyParameter(Pair pair) implements StrategyParameter {
        @Override
        public StrategyCreationParameter strategyParameter() {
            return null;
        }

        @Override
        public Set<? extends TradeableMarketRegime> activeOnRegimes() {
            return Set.of();
        }

        @Override
        public ScopedExternalServiceRegistry scopedExternalServiceRegistry() {
            throw new UnsupportedOperationException("Not implemented yet");
        }

        @Override
        public StrategyCreationParameter clone() {
            return new PairStrategyParameter(pair);
        }
    }
}
