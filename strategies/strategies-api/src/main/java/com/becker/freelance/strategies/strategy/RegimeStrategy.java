package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;
import com.becker.freelance.strategies.executionparameter.EntryExecutionParameter;
import com.becker.freelance.strategies.executionparameter.ExitExecutionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;

import java.util.*;

public class RegimeStrategy extends BaseStrategy {

    private static final Logger logger = LoggerFactory.getLogger(RegimeStrategy.class);

    private final Map<QuantileMarketRegime, List<BaseStrategy>> strategiesByRegime;
    private final List<BaseStrategy> allStrategies;

    public RegimeStrategy(Pair pair, Map<QuantileMarketRegime, List<BaseStrategy>> strategiesByRegime) {
        super(new PairStrategyParameter(pair));
        this.strategiesByRegime = new HashMap<>();

        for (QuantileMarketRegime marketRegime : QuantileMarketRegime.all()) {
            this.strategiesByRegime.put(marketRegime, strategiesByRegime.getOrDefault(marketRegime, new ArrayList<>()));
        }

        this.allStrategies = strategiesByRegime.values().stream().flatMap(Collection::stream).toList();
    }


    @Override
    protected Optional<EntrySignalBuilder> internalShouldEnter(EntryExecutionParameter entryParameter) {
        QuantileMarketRegime currentMarketRegime = currentMarketRegime();

        logger.debug("Current market regime is {}", currentMarketRegime.name());

        for (BaseStrategy baseStrategy : strategiesByRegime.get(currentMarketRegime)) {
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
        QuantileMarketRegime currentMarketRegime = currentMarketRegime();
        for (BaseStrategy baseStrategy : strategiesByRegime.get(currentMarketRegime)) {
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
                        .map(BaseStrategy::unstableBars)
                        .orElse(0)
        );
    }

    @Override
    public void setOpenPositionRequestor(OpenPositionRequestor openPositionRequestor) {
        super.setOpenPositionRequestor(openPositionRequestor);
        allStrategies.forEach(strategy -> strategy.setOpenPositionRequestor(openPositionRequestor));
    }

    @Override
    protected void addBarIfNeeded(Bar currentPrice) {
        super.addBarIfNeeded(currentPrice);
        allStrategies.forEach(strategy -> strategy.addBarIfNeeded(currentPrice));
    }

    private static final record PairStrategyParameter(Pair pair) implements StrategyParameter {
        @Override
        public StrategyCreationParameter strategyParameter() {
            return null;
        }

        @Override
        public Set<? extends TradeableQuantilMarketRegime> activeOnRegimes() {
            return Set.of();
        }

        @Override
        public StrategyCreationParameter clone() {
            return new PairStrategyParameter(pair);
        }
    }
}
