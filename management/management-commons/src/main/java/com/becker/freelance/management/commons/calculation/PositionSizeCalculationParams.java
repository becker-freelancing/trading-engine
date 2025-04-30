package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

public record PositionSizeCalculationParams(Decimal stopLossInPoints, Pair pair) {
}
