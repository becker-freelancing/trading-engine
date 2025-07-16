package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.MaxRiskValidator;
import com.becker.freelance.management.commons.validation.MaxRiskValidatorParams;
import com.becker.freelance.management.impl.adaption.PositionSizeAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxRiskEntrySignalValidation implements EntrySignalValidator {

    private static final Logger logger = LoggerFactory.getLogger(PositionSizeAdaptor.class);
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
        boolean valid = maxRiskValidator.isValid(environmentProvider, maxRiskValidatorParams);
        logger.debug("Max Risk is valid: {}. Calculation Params: {}", valid, maxRiskValidatorParams);
        return valid;
    }

}
