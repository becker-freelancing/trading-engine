package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.MaxDrawdownValidator;

public class MaxDrawdownEntrySignalValidation implements EntrySignalValidator {

    private final MaxDrawdownValidator maxDrawdownValidator;

    public MaxDrawdownEntrySignalValidation() {
        this.maxDrawdownValidator = new MaxDrawdownValidator();
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return maxDrawdownValid(environmentProvider, entrySignal);
    }

    private boolean maxDrawdownValid(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return maxDrawdownValidator.isValid(environmentProvider, entrySignal.pair());
    }

}
