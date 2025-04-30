package com.becker.freelance.management.commons.validation;

import com.becker.freelance.math.Decimal;

public record ChanceRiskRatioValidatorParams(Decimal takeProfitInPoints, Decimal stopLossInPoints) {
}
