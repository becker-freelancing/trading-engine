package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidator;
import com.becker.freelance.management.commons.validation.ChanceRiskRatioValidatorParams;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChanceRiskEntrySignalValidation implements EntrySignalValidator {

    private static final Logger logger = LoggerFactory.getLogger(ChanceRiskEntrySignalValidation.class);
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
        logger.debug("Stop loss in points {}", stopLossInPoints);
        if (stopLossInPoints.isEqualToZero()){
            return false;
        }
        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(
                entrySignal.estimatedLimitInPoints(environmentProvider.getCurrentPrice(entrySignal.pair())),
                stopLossInPoints);
        boolean valid = chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);
        logger.debug("Chance Risk Ratio is valid: {}. Calculation params: {}", valid, chanceRiskRatioValidatorParams);
        return valid;
    }
}
