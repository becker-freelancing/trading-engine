package com.becker.freelance.management.api.environment;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.ClosedTradesRequestor;
import com.becker.freelance.opentrades.OpenPositionRequestor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class FileManagementEnvironmentProvider implements ManagementEnvironmentProvider {

    private final Decimal maxRiskPerTrade;
    private final Decimal preferredRiskPerTrade;
    private final Decimal maxTotalRisk;
    private final Decimal minChanceRiskRatio;
    private final List<MaxDrawdown> maxDrawdowns;
    private final AccountBalanceRequestor accountBalanceRequestor;
    private final BrokerSpecificsRequestor brokerSpecificsRequestor;
    private final OpenPositionRequestor openPositionRequestor;
    private final ClosedTradesRequestor closedTradesRequestor;
    private final EurUsdRequestor eurUsdRequestor;

    public FileManagementEnvironmentProvider(AccountBalanceRequestor accountBalanceRequestor, BrokerSpecificsRequestor brokerSpecificsRequestor, OpenPositionRequestor openPositionRequestor, ClosedTradesRequestor closedTradesRequestor, EurUsdRequestor eurUsdRequestor) {
        this.accountBalanceRequestor = accountBalanceRequestor;
        this.brokerSpecificsRequestor = brokerSpecificsRequestor;
        this.openPositionRequestor = openPositionRequestor;
        this.closedTradesRequestor = closedTradesRequestor;
        this.eurUsdRequestor = eurUsdRequestor;
        JSONObject managementConfig;
        try {
            managementConfig = new JSONObject(new String(FileManagementEnvironmentProvider.class.getClassLoader().getResourceAsStream("management-config.json").readAllBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file 'management-config.json' in classpath", e);
        }

        this.maxRiskPerTrade = new Decimal(managementConfig.getDouble("maxRiskPerTrade"));
        this.preferredRiskPerTrade = managementConfig.has("preferredRiskPerTrade") ? new Decimal(managementConfig.getDouble("preferredRiskPerTrade")) : null;
        this.maxTotalRisk = managementConfig.has("maxTotalRisk") ? new Decimal(managementConfig.getDouble("maxTotalRisk")) : null;
        this.minChanceRiskRatio = managementConfig.has("minChanceRiskRatio") ? new Decimal(managementConfig.getDouble("minChanceRiskRatio")) : null;
        this.maxDrawdowns = managementConfig.has("maxDrawdowns") ? map(managementConfig.getJSONArray("maxDrawdowns")) : List.of();
    }

    private List<MaxDrawdown> map(JSONArray maxDrawdowns) {
        return IntStream.range(0, maxDrawdowns.length())
                .mapToObj(maxDrawdowns::getJSONObject)
                .map(drawdown -> new MaxDrawdown(new Decimal(drawdown.getDouble("drawdown")), Duration.ofSeconds(drawdown.getLong("durationSec"))))
                .toList();
    }

    @Override
    public Decimal getMaxRiskPerTrade() {
        return maxRiskPerTrade;
    }

    @Override
    public Optional<Decimal> getPreferredRiskPerTrade() {
        return Optional.ofNullable(preferredRiskPerTrade);
    }

    @Override
    public Optional<Decimal> getMaxTotalRisk() {
        return Optional.ofNullable(maxTotalRisk);
    }

    @Override
    public Optional<Decimal> getMinChanceRiskRatio() {
        return Optional.ofNullable(minChanceRiskRatio);
    }

    @Override
    public List<MaxDrawdown> getMaxDrawdowns() {
        return maxDrawdowns;
    }

    @Override
    public Decimal getCurrentAccountBalance() {
        return accountBalanceRequestor.getCurrentAccountBalance();
    }

    @Override
    public Integer getMaxBrokerOrderFractionPlaces() {
        return brokerSpecificsRequestor.getMaxBrokerFractionPlaces();
    }

    @Override
    public List<Position> getOpenPositions() {
        return openPositionRequestor.getOpenPositions();
    }

    @Override
    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair) {
        return closedTradesRequestor.getTradesForDurationUntilNowForPair(duration, pair);
    }

    @Override
    public EurUsdRequestor getEurUsdRequestor() {
        return eurUsdRequestor;
    }
}
