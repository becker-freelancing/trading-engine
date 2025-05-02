package com.becker.freelance.management.commons.validation;

import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ChanceRiskRatioValidatorTest {

    Validator<ChanceRiskRatioValidatorParams> chanceRiskRatioValidator;

    @BeforeEach
    void setUp() {
        chanceRiskRatioValidator = new ChanceRiskRatioValidator();
    }

    @Test
    void validIfChanceRiskRatioIsGreater() {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(Optional.of(Decimal.valueOf(2))).when(environmentProvider).getMinChanceRiskRatio();

        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(Decimal.valueOf(4), Decimal.valueOf(2));

        boolean valid = chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);

        assertTrue(valid);
    }

    @Test
    void notValidIfChanceRiskRatioIsLess() {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(Optional.of(Decimal.valueOf(2))).when(environmentProvider).getMinChanceRiskRatio();

        ChanceRiskRatioValidatorParams chanceRiskRatioValidatorParams = new ChanceRiskRatioValidatorParams(Decimal.valueOf(4), Decimal.valueOf(2.000001));

        boolean valid = chanceRiskRatioValidator.isValid(environmentProvider, chanceRiskRatioValidatorParams);

        assertTrue(valid);
    }

    @Test
    void validIfNoMinChanceRiskRatioIsProvided() {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(Optional.empty()).when(environmentProvider).getMinChanceRiskRatio();

        boolean valid = chanceRiskRatioValidator.isValid(environmentProvider, null);

        assertTrue(valid);
    }

}