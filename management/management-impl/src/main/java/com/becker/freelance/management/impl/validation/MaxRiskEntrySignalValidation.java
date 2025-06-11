package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.MaxRiskValidator;
import com.becker.freelance.management.commons.validation.MaxRiskValidatorParams;

public class MaxRiskEntrySignalValidation implements EntrySignalValidator {

    private final MaxRiskValidator maxRiskValidator;

    public MaxRiskEntrySignalValidation() {
        this.maxRiskValidator = new MaxRiskValidator();
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return maxRiskValid(environmentProvider, entrySignal);
    }

    private boolean maxRiskValid(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        MaxRiskValidatorParams maxRiskValidatorParams = new MaxRiskValidatorParams(entrySignal.size(),
                entrySignal.estimatedStopInPoints(environmentProvider.getCurrentPrice(entrySignal.pair())),
                entrySignal.pair());
        return maxRiskValidator.isValid(environmentProvider, maxRiskValidatorParams);
    }

}
