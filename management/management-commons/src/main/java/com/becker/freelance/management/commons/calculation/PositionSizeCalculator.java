package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;

public class PositionSizeCalculator implements Calculator<Decimal, PositionSizeCalculationParams> {

    @Override
    public Decimal calculate(ManagementEnvironmentProvider environmentProvider, PositionSizeCalculationParams positionSizeCalculationParams) {
        Decimal riskPerTradeInPercent = environmentProvider.getPreferredRiskPerTrade().orElse(environmentProvider.getMaxRiskPerTrade());
        Decimal riskPerTrade = riskPerTradeInPercent.multiply(environmentProvider.getCurrentAccountBalance());
        Decimal profitPerPoint = positionSizeCalculationParams.pair().profitPerPointForOneContract();
        return riskPerTrade.divide(positionSizeCalculationParams.stopLossInPoints().multiply(profitPerPoint));
    }
}
