package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.*;

public class EntrySignalValidation implements EntrySignalValidator {

    private final ChanceRiskRatioValidator chanceRiskRatioValidator;
    private final MaxDrawdownValidator maxDrawdownValidator;
    private final MaxRiskValidator maxRiskValidator;

    public EntrySignalValidation() {
        this.chanceRiskRatioValidator = new ChanceRiskRatioValidator();
        this.maxDrawdownValidator = new MaxDrawdownValidator();
        this.maxRiskValidator = new MaxRiskValidator();
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        LevelEntrySignal levelEntrySignal = entrySignal.toLevelEntrySignal(environmentProvider.getEurUsdRequestor());
        if (!chanceRiskValid(environmentProvider, levelEntrySignal)) {
            return false;
        }
        if (!maxDrawdownValid(environmentProvider, entrySignal)) {
            return false;
        }
        if (!maxRiskValid(environmentProvider, levelEntrySignal)) {
            return false;
        }

        return true;
    }

    private boolean maxRiskValid(ManagementEnvironmentProvider environmentProvider, LevelEntrySignal levelEntrySignal) {
        MaxRiskValidatorParams maxRiskValidatorParams = new MaxRiskValidatorParams(levelEntrySignal.size(), levelEntrySignal.stopInPoints(), levelEntrySignal.pair());
        return maxRiskValidator.isValid(environmentProvider, maxRiskValidatorParams);
    }

    private boolean maxDrawdownValid(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return maxDrawdownValidator.isValid(environmentProvider, entrySignal.pair());
    }

    private boolean chanceRiskValid(ManagementEnvironmentProvider environmentProvider, LevelEntrySignal levelEntrySignal) {
        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(levelEntrySignal.limitInPoints(), levelEntrySignal.stopInPoints());
        return chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);
    }
}
