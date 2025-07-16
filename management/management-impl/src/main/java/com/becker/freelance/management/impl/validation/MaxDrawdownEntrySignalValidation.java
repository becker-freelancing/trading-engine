package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.MaxDrawdownValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxDrawdownEntrySignalValidation implements EntrySignalValidator {

    private static final Logger logger = LoggerFactory.getLogger(MaxDrawdownEntrySignalValidation.class);
    private final MaxDrawdownValidator maxDrawdownValidator;

    public MaxDrawdownEntrySignalValidation() {
        this.maxDrawdownValidator = new MaxDrawdownValidator();
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return maxDrawdownValid(environmentProvider, entrySignal);
    }

    private boolean maxDrawdownValid(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        boolean valid = maxDrawdownValidator.isValid(environmentProvider, entrySignal.pair());
        logger.debug("Max Drawdown is valid: {}", valid);
        return valid;
    }

}
