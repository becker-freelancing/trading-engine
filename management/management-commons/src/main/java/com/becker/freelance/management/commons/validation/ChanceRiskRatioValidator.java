package com.becker.freelance.management.commons.validation;

import com.becker.freelance.management.api.EnvironmentProvider;
import com.becker.freelance.math.Decimal;

import java.util.Optional;

public class ChanceRiskRatioValidator implements Validator<ChanceRiskRatioValidatorParams> {

    @Override
    public boolean isValid(EnvironmentProvider environmentProvider, ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams) {
        Optional<Decimal> minChanceRiskRatio = environmentProvider.getMinChanceRiskRatio();
        if (minChanceRiskRatio.isEmpty()) {
            return true;
        }

        Decimal actualChanceRiskRatio = chanceRiskRatioValidatorParams.takeProfitInPoints().divide(chanceRiskRatioValidatorParams.stopLossInPoints());

        return actualChanceRiskRatio.isGreaterThanOrEqualTo(minChanceRiskRatio.get());
    }
}
