package com.becker.freelance.backtest;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.CompleteTimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.strategies.strategy.Initializable;
import com.becker.freelance.strategies.strategy.TradingStrategyInitiator;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

class BacktestStrategyInitiator implements TradingStrategyInitiator {

    private static final Logger logger = LoggerFactory.getLogger(BacktestStrategyInitiator.class);

    private final PriceRequestorBroker priceRequestorBroker;

    BacktestStrategyInitiator(PriceRequestorBroker priceRequestorBroker) {
        this.priceRequestorBroker = priceRequestorBroker;
    }

    @Override
    public void initiate(Initializable initializable, LocalDateTime currentTime) {
        int unstableBars = initializable.initializationBarCount();
        Pair pair = initializable.getPair();
        Duration pairDuration = pair.toDuration();
        LocalDateTime start = currentTime.minus(pairDuration.multipliedBy(unstableBars));
        LocalDateTime end = currentTime.minus(pairDuration);
        logger.info("Trying to get prices from {} to {} ({} Bars on pair {})", start, end, unstableBars, pair.technicalName());
        List<TimeSeriesEntry> prices = priceRequestorBroker.forPair(pair).getPriceInRange(start, end).stream()
                .filter(Objects::nonNull)
                .toList();
        logger.info("Received {} of {} Bars on pair {}", prices.size(), unstableBars, pair.technicalName());
        CompleteTimeSeries timeSeries = new CompleteTimeSeries(pair, prices);
        initializable.processInitData(timeSeries);

        for (Initializable transitiveInitializable : initializable.getTransitiveInitializables()) {
            initiate(transitiveInitializable, currentTime);
        }

    }
}
