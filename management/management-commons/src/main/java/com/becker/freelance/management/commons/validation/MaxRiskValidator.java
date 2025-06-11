package com.becker.freelance.management.commons.validation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;

import java.util.Optional;

public class MaxRiskValidator implements Validator<MaxRiskValidatorParams> {
    @Override
    public boolean isValid(ManagementEnvironmentProvider environmentProvider, MaxRiskValidatorParams maxRiskValidatorParams) {
        Optional<Decimal> optionalMaxTotalRisk = environmentProvider.getMaxTotalRisk();
        if (optionalMaxTotalRisk.isEmpty()) {
            return true;
        }

        Decimal totalRiskOfOpenPositions = environmentProvider.getOpenPositions().stream()
                .map(position -> calculateRiskInPercent(position, environmentProvider))
                .reduce(Decimal::add)
                .orElse(Decimal.ZERO);

        Decimal riskOfPositionToOpen = calculateRiskInPercent(
                maxRiskValidatorParams.stopLossInPoints(),
                maxRiskValidatorParams.pair(),
                maxRiskValidatorParams.positionSize(),
                environmentProvider.getCurrentAccountBalance());

        Decimal totalRiskAfterPositionOpen = totalRiskOfOpenPositions.add(riskOfPositionToOpen);

        return totalRiskAfterPositionOpen.isLessThanOrEqualTo(optionalMaxTotalRisk.get());
    }

    private Decimal calculateRiskInPercent(Position position, ManagementEnvironmentProvider environmentProvider) {
        Pair pair = position.getPair();
        Decimal size = position.getSize();
        Decimal stopLevel = position.getEstimatedStopLevel(environmentProvider.getCurrentPrice(position.getPair()));
        Decimal openPrice = position.getOpenPrice();
        Decimal currentAccountBalance = environmentProvider.getCurrentAccountBalance();
        Decimal stopDistance = openPrice.subtract(stopLevel).abs();

        return calculateRiskInPercent(stopDistance, pair, size, currentAccountBalance);
    }

    private Decimal calculateRiskInPercent(Decimal stopDistance, Pair pair, Decimal size, Decimal currentAccountBalance) {

        Decimal profitPerPoint = pair.profitPerPointForOneContract().multiply(size).abs();
        Decimal stopDistanceInEuro = stopDistance.multiply(profitPerPoint);

        return stopDistanceInEuro.divide(currentAccountBalance);
    }
}
