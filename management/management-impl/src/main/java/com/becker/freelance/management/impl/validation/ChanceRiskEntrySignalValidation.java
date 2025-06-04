package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidatorParams;

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
        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(entrySignal.limitInPoints(), entrySignal.stopInPoints());
        return chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);
    }
}
