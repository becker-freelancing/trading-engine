package com.becker.freelance.management.api.environment;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ManagementEnvironmentProvider extends TimeChangeListener {

    public Decimal getMaxRiskPerTrade();

    public Optional<Decimal> getPreferredRiskPerTrade();

    public Optional<Decimal> getMaxTotalRisk();

    public Optional<Decimal> getMinChanceRiskRatio();

    public List<MaxDrawdown> getMaxDrawdowns();

    public Decimal getCurrentAccountBalance();

    public Integer getMaxBrokerOrderFractionPlaces();

    public Decimal getMaxPositionSize();

    public List<Position> getOpenPositions();

    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair);

    public EurUsdRequestor getEurUsdRequestor();

    public Decimal calculateMakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize);

    public Decimal calculateTakerTradingFeeInCounterCurrency(Decimal currentPrice, Decimal positionSize);

    public LocalDateTime currentTime();
}
