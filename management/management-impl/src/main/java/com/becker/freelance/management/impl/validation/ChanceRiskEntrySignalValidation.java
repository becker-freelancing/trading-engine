package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidatorParams;
import com.becker.freelance.math.Decimal;

public class ChanceRiskEntrySignalValidation implements EntrySignalValidator {

    private final ChanceRiskRatioValidator chanceRiskRatioValidator;

    public ChanceRiskEntrySignalValidation() {
        this.chanceRiskRatioValidator = new ChanceRiskRatioValidator();
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return chanceRiskValid(environmentProvider, entrySignal);
    }

    private boolean chanceRiskValid(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        Decimal stopLossInPoints = entrySignal.estimatedStopInPoints(environmentProvider.getCurrentPrice(entrySignal.pair()));
        if (stopLossInPoints.isEqualToZero()){
            return false;
        }
        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(
                entrySignal.estimatedLimitInPoints(environmentProvider.getCurrentPrice(entrySignal.pair())),
                stopLossInPoints);
        return chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);
    }
}
