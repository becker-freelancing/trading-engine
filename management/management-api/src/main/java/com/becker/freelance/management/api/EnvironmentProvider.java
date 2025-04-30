package com.becker.freelance.management.api;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface EnvironmentProvider {

    public Decimal getMaxRiskPerTrade();

    public Optional<Decimal> getPreferredRiskPerTrade();

    public Optional<Decimal> getMaxTotalRisk();

    public Optional<Decimal> getMinChanceRiskRatio();

    public List<MaxDrawdown> getMaxDrawDowns();

    public Decimal getCurrentAccountBalance();

    public Integer getMaxBrokerOrderFractionPlaces();

    public List<Position> getOpenPositions();

    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair);
}
