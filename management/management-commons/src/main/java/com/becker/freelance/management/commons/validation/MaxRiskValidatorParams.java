package com.becker.freelance.management.commons.validation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

public record MaxRiskValidatorParams(Decimal positionSize, Decimal stopLossInPoints, Pair pair) {
}
